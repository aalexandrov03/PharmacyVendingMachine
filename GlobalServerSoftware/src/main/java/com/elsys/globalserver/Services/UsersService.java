package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Admin;
import com.elsys.globalserver.DB_Entities.Patient;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DataAccess.AdminRepository;
import com.elsys.globalserver.DataAccess.PatientsRepository;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.Exceptions.Users.AdminAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.DoctorAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.PatientAlreadyExistsException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class UsersService {
    private final PatientsRepository casualUserRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;

    @Autowired
    public UsersService(PatientsRepository casualUserRepository,
                        DoctorRepository doctorRepository,
                        AdminRepository adminRepository) {
        this.casualUserRepository = casualUserRepository;
        this.doctorRepository = doctorRepository;
        this.adminRepository = adminRepository;
    }

    public void registerPatient(Patient user_data) throws PatientAlreadyExistsException{
        Optional<Patient> user = casualUserRepository.findByUsername(user_data.getUsername());

        if (user.isPresent())
            throw new PatientAlreadyExistsException().byUsername(user_data.getUsername());

        casualUserRepository.save(user_data);
    }

    public Optional<Patient> loginPatient(String username, String password) {
        return casualUserRepository.findByUsernameAndPassword(username, password);
    }

    public boolean registerDoctor(Doctor doctor_data) throws DoctorAlreadyExistsException {
        Optional<Doctor> doctor = doctorRepository.findByUsername(doctor_data.getUsername());
        if (doctor.isPresent())
            throw new DoctorAlreadyExistsException();

        boolean status = checkDoctor(doctor_data);
        if (status)
            doctorRepository.save(doctor_data);

        return status;
    }

    public Optional<Doctor> loginDoctor(String username, String password) {
        return doctorRepository.findByUsernameAndPassword(username, password);
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

        HtmlTable node = (HtmlTable) page.getByXPath("//table[@class='items']").get(0);

        if (node.getRowCount() == 1)
            return false;

        String name = node.getRow(1).getCell(2).asNormalizedText();
        return name.contains(doctor.getFullName());
    }

    public void registerAdmin(String username, String password) throws AdminAlreadyExistsException {
        if (adminRepository.findByUsernameAndPassword(username, password).isPresent())
            throw new AdminAlreadyExistsException();

        adminRepository.save(new Admin(username, password));
    }

    public boolean authenticateAdmin(String username, String password) {
        Optional<Admin> admin = adminRepository.findByUsernameAndPassword(username, password);
        return admin.isPresent();
    }
}
