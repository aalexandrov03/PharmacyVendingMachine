package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.UserRepository;
import com.elsys.globalserver.DatabaseEntities.User;
import com.elsys.globalserver.Exceptions.Users.*;
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
    public UsersService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerPatient(User patient) throws PatientAlreadyExistsException{
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        Optional<User> user = userRepository.findByUsername(patient.getUsername());

        if (user.isPresent())
            throw new PatientAlreadyExistsException();

        userRepository.save(patient);
    }

    public User getPatientInfo(String username) throws PatientNotFoundException {
        Optional<User> patient = userRepository.findByUsername(username);

        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return patient.get();
    }

    public void registerDoctor(User doctor_data) throws DoctorAlreadyExistsException {
        doctor_data.setPassword(passwordEncoder.encode(doctor_data.getPassword()));
        Optional<User> doctor = userRepository.findByUsername(doctor_data.getUsername());

        if (doctor.isPresent())
            throw new DoctorAlreadyExistsException();

        boolean status = checkDoctor(doctor_data);

        if (status)
            userRepository.save(doctor_data);

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

        HtmlTable node = (HtmlTable) page.getByXPath("//table[@class='items']").get(0);

        if (node.getRowCount() == 1)
            return false;

        String name = node.getRow(1).getCell(2).asNormalizedText();
        return name.contains(doctor.getFullname());
    }

    public User getDoctorInfo(String username) throws DoctorNotFoundException {
        Optional<User> doctor = userRepository.findByUsername(username);

        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return doctor.get();
    }
}
