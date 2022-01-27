package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Patient;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.DataAccess.PatientsRepository;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.MedicinesRepository;
import com.elsys.globalserver.DataAccess.PrescriptionsRepository;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PrescriptionsService {
    private final PrescriptionsRepository prescriptionsRepository;
    private final PatientsRepository casualUserRepository;
    private final DoctorRepository doctorRepository;
    private final MedicinesRepository medicinesRepository;

    @Autowired
    public PrescriptionsService(PrescriptionsRepository prescriptionsRepository,
                                PatientsRepository casualUserRepository,
                                DoctorRepository doctorRepository,
                                MedicinesRepository medicinesRepository) {
        this.prescriptionsRepository = prescriptionsRepository;
        this.casualUserRepository = casualUserRepository;
        this.doctorRepository = doctorRepository;
        this.medicinesRepository = medicinesRepository;
    }

    public void addPrescription(String username,
                                   int doctor_id,
                                   List<Integer> med_ids)
            throws MedicineNotFoundException, PatientNotFoundException, DoctorNotFoundException{
        Optional<Patient> user = casualUserRepository.findByUsername(username);
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);
        List<Medicine> medicines = new ArrayList<>();

        for (int med_id : med_ids) {
            Optional<Medicine> medicine = medicinesRepository.findById(med_id);

            if (medicine.isEmpty())
                throw new MedicineNotFoundException(med_id);

            medicines.add(medicine.get());
        }

        Prescription prescription = new Prescription();

        if (user.isEmpty())
            throw new PatientNotFoundException().byUsername(username);
        if (doctor.isEmpty())
            throw new DoctorNotFoundException().byID(doctor_id);

        prescription.setUser(user.get());
        prescription.setDoctor(doctor.get());

        for (Medicine medicine : medicines)
            prescription.addMedicine(medicine);

        prescriptionsRepository.save(prescription);
    }

    public Set<Prescription> getDoctorPrescriptions(int doctor_id) throws DoctorNotFoundException{
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);

        if (doctor.isEmpty())
            throw new DoctorNotFoundException().byID(doctor_id);

        return doctor.get().getPrescriptions();
    }

    public Set<Prescription> getUserPrescriptions(int user_id) throws PatientNotFoundException{
        Optional<Patient> user = casualUserRepository.findById(user_id);

        if (user.isEmpty())
            throw new PatientNotFoundException().byID(user_id);

        return user.get().getPrescriptions();
    }

    public List<Prescription> getAllPrescriptions(){
        return StreamSupport.stream(prescriptionsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void invalidatePrescriptions(List<Integer> prescription_ids) throws PrescriptionNotFoundException {
        for (int prescription_id : prescription_ids){
            Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

            if (prescription.isEmpty())
                throw new PrescriptionNotFoundException(prescription_id);

            prescription.get().setValid(!prescription.get().isValid());
            prescriptionsRepository.save(prescription.get());
        }
    }

    public void executePrescriptions(List<Integer> prescription_ids) throws PrescriptionNotFoundException{
        for (int prescription_id : prescription_ids){
            Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

            if (prescription.isEmpty())
                throw new PrescriptionNotFoundException(prescription_id);

            prescription.get().setExecuted(true);
            prescriptionsRepository.save(prescription.get());
        }
    }

    public void deletePrescriptions(List<Integer> prescription_ids) throws PrescriptionNotFoundException{
        for (int prescription_id : prescription_ids) {
            Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

            if (prescription.isEmpty())
                throw new PrescriptionNotFoundException(prescription_id);
        }
        prescriptionsRepository.deleteAllById(prescription_ids);
    }
}
