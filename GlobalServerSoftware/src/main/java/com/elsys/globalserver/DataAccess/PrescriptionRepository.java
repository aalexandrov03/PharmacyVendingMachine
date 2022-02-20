package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Prescription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrescriptionRepository extends CrudRepository<Prescription, Integer> {
    List<Prescription> findByDoctorId(int doctor_id);
    List<Prescription> findByPatientId(int patientId);
}
