package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DataAccess.BugsRepository;
import com.elsys.globalserver.DataAccess.CasualUserRepository;
import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.DataAccess.PrescriptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CasualUsersService {
    private final CasualUserRepository casualUserRepository;
    private final BugsRepository bugsRepository;
    private final PrescriptionsRepository prescriptionsRepository;

    @Autowired
    public CasualUsersService(CasualUserRepository casualUserRepository,
                              BugsRepository bugsRepository,
                              PrescriptionsRepository prescriptionsRepository) {
        this.casualUserRepository = casualUserRepository;
        this.bugsRepository = bugsRepository;
        this.prescriptionsRepository = prescriptionsRepository;
    }

    public boolean register(CasualUser user_data) {
        Optional<CasualUser> user = casualUserRepository.findByUsername(user_data.getUsername());

        if (user.isPresent())
            return false;

        casualUserRepository.save(user_data);
        return true;
    }

    public Optional<CasualUser> login(String username, String password) {
        return casualUserRepository.findByUsernameAndPassword(username, password);
    }

    public Set<Prescription> getAllUserPrescriptions(int user_id) {
        Optional<CasualUser> user = casualUserRepository.findById(user_id);
        return user.map(CasualUser::getPrescriptions).orElse(null);
    }

    public void reportBug(Bug bug) {
        bugsRepository.save(bug);
    }

    public boolean invalidatePrescription(int prescription_id){
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

        if (prescription.isEmpty())
            return false;

        prescription.get().setValid(false);
        prescriptionsRepository.save(prescription.get());
        return true;
    }
}
