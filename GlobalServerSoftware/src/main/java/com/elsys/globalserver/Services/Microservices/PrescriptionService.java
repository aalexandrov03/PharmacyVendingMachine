package com.elsys.globalserver.Services.Microservices;

import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.DataAccess.CasualUserRepository;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.MedicinesRepository;
import com.elsys.globalserver.DataAccess.PrescriptionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PrescriptionService {
    private final PrescriptionsRepository prescriptionsRepository;
    private final CasualUserRepository casualUserRepository;
    private final DoctorRepository doctorRepository;
    private final MedicinesRepository medicinesRepository;

    @Autowired
    public PrescriptionService(PrescriptionsRepository prescriptionsRepository,
                               CasualUserRepository casualUserRepository,
                               DoctorRepository doctorRepository,
                               MedicinesRepository medicinesRepository) {
        this.prescriptionsRepository = prescriptionsRepository;
        this.casualUserRepository = casualUserRepository;
        this.doctorRepository = doctorRepository;
        this.medicinesRepository = medicinesRepository;
    }

    public boolean addPrescription(String username,
                                   int doctor_id,
                                   List<Integer> med_ids) {
        Optional<CasualUser> user = casualUserRepository.findByUsername(username);
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);
        List<Medicine> medicines = new ArrayList<>();

        for (int med_id : med_ids) {
            Optional<Medicine> medicine = medicinesRepository.findById(med_id);

            if (medicine.isEmpty())
                return false;

            medicines.add(medicine.get());
        }

        Prescription prescription = new Prescription();

        if (user.isPresent() && doctor.isPresent()) {
            prescription.setUser(user.get());
            prescription.setDoctor(doctor.get());

            for (Medicine medicine : medicines)
                prescription.addMedicine(medicine);

            prescriptionsRepository.save(prescription);
            return true;
        }

        return false;
    }

    public Set<Prescription> getDoctorPrescriptions(int doctor_id) {
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);
        return doctor.map(Doctor::getPrescriptions).orElse(null);
    }

    public Set<Prescription> getUserPrescriptions(int user_id) {
        Optional<CasualUser> user = casualUserRepository.findById(user_id);
        return user.map(CasualUser::getPrescriptions).orElse(null);
    }

    public boolean invalidatePrescription(int prescription_id){
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

        if (prescription.isEmpty())
            return false;

        prescription.get().setValid(false);
        prescriptionsRepository.save(prescription.get());
        return true;
    }

    public boolean deletePrescription(int prescription_id){
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

        if (prescription.isEmpty())
            return false;

        prescriptionsRepository.deleteById(prescription_id);
        return true;
    }
}
