package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Patient;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PatientsRepository extends CrudRepository<Patient, Integer> {
    Optional<Patient> findByUsername(String username);
    Optional<Patient> findByUsernameAndPassword(String username, String password);
}
