package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.UserRepository;
import com.elsys.globalserver.Exceptions.Users.*;
import com.elsys.globalserver.Models.User;
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
public class UsersService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsersService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerPatient(User patient) throws PatientAlreadyExistsException{
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        Optional<User> patient1 = userRepository.findUserByEmail(patient.getEmail());

        if (patient1.isPresent())
            throw new PatientAlreadyExistsException();

        userRepository.save(patient);
    }

    public User getPatientInfo(String email) throws PatientNotFoundException {
        Optional<User> patient = userRepository.findUserByEmail(email);

        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return patient.get();
    }

    public void registerDoctor(User doctor) throws Exception {
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        Optional<User> doctor1 = userRepository.findUserByEmail(doctor.getEmail());

        if (doctor1.isPresent())
            throw new DoctorAlreadyExistsException();

        boolean status = checkDoctor(doctor);

        if (status)
            userRepository.save(doctor);
        else
            throw new Exception("Doctor check failed!");
    }

    private boolean checkDoctor(User doctor) {
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

        String name = node.getRow(1).getCell(2).asNormalizedText();
        return name.contains(doctor.getFullName());
    }

    public User getDoctorInfo(String email) throws DoctorNotFoundException {
        Optional<User> doctor = userRepository.findUserByEmail(email);

        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return doctor.get();
    }
}
