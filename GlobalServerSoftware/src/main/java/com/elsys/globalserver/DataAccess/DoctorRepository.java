package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Doctor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends CrudRepository<Doctor, Integer> {
    Optional<Doctor> findByEmail(String email);
}
