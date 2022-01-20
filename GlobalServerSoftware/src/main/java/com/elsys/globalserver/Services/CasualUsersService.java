package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.Services.Microservices.AuthenticationService;
import com.elsys.globalserver.Services.Microservices.BugService;
import com.elsys.globalserver.Services.Microservices.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CasualUsersService {
    private final AuthenticationService authenticationService;
    private final PrescriptionService prescriptionService;
    private final BugService bugService;

    @Autowired
    public CasualUsersService(AuthenticationService authenticationService,
                              PrescriptionService prescriptionService,
                              BugService bugService){
        this.authenticationService = authenticationService;
        this.prescriptionService = prescriptionService;
        this.bugService = bugService;
    }

    public boolean register(CasualUser user_data) {
        return authenticationService.registerCasualUser(user_data);
    }

    public Optional<CasualUser> login(String username, String password) {
        return authenticationService.loginCasualUser(username, password);
    }

    public Set<Prescription> getAllUserPrescriptions(int user_id) {
        return prescriptionService.getUserPrescriptions(user_id);
    }

    public void reportBug(Bug bug) {
        bugService.reportBug(bug);
    }
}
