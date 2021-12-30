package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Doctor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DoctorRepository extends CrudRepository<Doctor, Integer> {
    Optional<Doctor> findByUsername(String username);
    Optional<Doctor> findByUsernameAndPassword(String username, String password);
}
