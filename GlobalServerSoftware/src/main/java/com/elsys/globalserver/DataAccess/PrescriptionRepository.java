package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Prescription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionRepository extends CrudRepository<Prescription, Integer> {
    List<Prescription> findByDoctor(String doctor);
    List<Prescription> findByPatient(String patient);
}
