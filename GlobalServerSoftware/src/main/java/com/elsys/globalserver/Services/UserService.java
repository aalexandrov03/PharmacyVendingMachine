package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.Exceptions.Users.*;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Patient;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerPatient(Patient patient) throws PatientAlreadyExistsException{
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        Optional<Patient> patient1 = patientRepository.findByEmail(patient.getEmail());

        if (patient1.isPresent())
            throw new PatientAlreadyExistsException();

        patientRepository.save(patient);
    }

    public void registerDoctor(Doctor doctor) throws Exception {
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        Optional<Doctor> doctor1 = doctorRepository.findByEmail(doctor.getEmail());

        if (doctor1.isPresent())
            throw new DoctorAlreadyExistsException();

        boolean status = checkDoctor(doctor);

        if (status)
            doctorRepository.save(doctor);
        else
            throw new Exception("Doctor check failed!");
    }

    public Patient getPatientInfo(String email) throws PatientNotFoundException {
        Optional<Patient> patient = patientRepository.findByEmail(email);

        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return patient.get();
    }

    public Doctor getDoctorInfo(String email) throws DoctorNotFoundException {
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);

        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return doctor.get();
    }

    private boolean checkDoctor(Doctor doctor) {
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);

        HtmlPage page;
        try {
            page = client.getPage("https://blsbg.eu/bg/medics/search?DocSearch[uin]=" + doctor.getUin());
        } catch (IOException e) {
            return false;
        } finally {
            client.close();
        }

        HtmlTable node;
        try{
            node = (HtmlTable) page.getByXPath("//table[@class='items']").get(0);
        } catch (IndexOutOfBoundsException e){
            return false;
        }

        if (node.getRowCount() == 1)
            return false;

        String name = node.getRow(1).getCell(2).asNormalizedText().replace("д-р ", "");
        String region = node.getRow(1).getCell(3).asNormalizedText();

        return name.equals(doctor.getFullName()) && region.equals(doctor.getRegion());
    }
}
