package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Patient;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Integer> {
    Optional<Patient> findByEmail(String email);
}
