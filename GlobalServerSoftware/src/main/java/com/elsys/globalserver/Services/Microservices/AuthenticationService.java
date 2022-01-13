package com.elsys.globalserver.Services.Microservices;

import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DataAccess.CasualUserRepository;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final CasualUserRepository casualUserRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public AuthenticationService(CasualUserRepository casualUserRepository,
                                 DoctorRepository doctorRepository) {
        this.casualUserRepository = casualUserRepository;
        this.doctorRepository = doctorRepository;
    }

    public boolean registerCasualUser(CasualUser user_data) {
        Optional<CasualUser> user = casualUserRepository.findByUsername(user_data.getUsername());

        if (user.isPresent())
            return false;

        casualUserRepository.save(user_data);
        return true;
    }

    public Optional<CasualUser> loginCasualUser(String username, String password) {
        return casualUserRepository.findByUsernameAndPassword(username, password);
    }

    public boolean registerDoctor(Doctor doctor_data) {
        Optional<Doctor> doctor = doctorRepository.findByUsername(doctor_data.getUsername());
        if (doctor.isPresent())
            return false;

        boolean status = checkDoctor(doctor_data);
        if (status)
            doctorRepository.save(doctor_data);

        return status;
    }

    public Optional<Doctor> loginDoctor(String username, String password) {
        return doctorRepository.findByUsernameAndPassword(username, password);
    }

    private boolean checkDoctor(Doctor doctor){
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);

        HtmlPage page;
        try {
            page = client.getPage("https://blsbg.eu/bg/medics/search?DocSearch[uin]="+doctor.getUin());
        } catch (IOException e) {
            return false;
        }
        HtmlTable node = (HtmlTable) page.getByXPath("//table[@class='items']").get(0);

        if (node.getRowCount() == 1)
            return false;

        String name = node.getRow(1).getCell(2).asNormalizedText();
        return name.equals(doctor.getFullName());
    }
}
