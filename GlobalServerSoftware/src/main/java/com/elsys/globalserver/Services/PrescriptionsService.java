package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.DataAccess.PrescriptionRepository;
import com.elsys.globalserver.DataAccess.UserRepository;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Prescription;
import com.elsys.globalserver.Models.User;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PrescriptionsService {
    private final PrescriptionRepository prescriptionsRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicinesRepository;

    @Autowired
    public PrescriptionsService(PrescriptionRepository prescriptionsRepository,
                                UserRepository userRepository,
                                MedicineRepository medicinesRepository) {
        this.prescriptionsRepository = prescriptionsRepository;
        this.userRepository = userRepository;
        this.medicinesRepository = medicinesRepository;
    }

    public void addPrescription(String patient_username, String doctor_username, List<Integer> med_ids)
            throws MedicineNotFoundException, PatientNotFoundException, DoctorNotFoundException{
        Optional<User> patient = userRepository.findByUsername(patient_username);
        Optional<User> doctor = userRepository.findByUsername(doctor_username);

        List<Medicine> medicines = new ArrayList<>();

        for (int med_id : med_ids) {
            Optional<Medicine> medicine = medicinesRepository.findById(med_id);

            if (medicine.isEmpty())
                throw new MedicineNotFoundException(med_id);

            medicines.add(medicine.get());
        }

        if (patient.isEmpty())
            throw new PatientNotFoundException();
        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        Prescription prescription = new Prescription(doctor.get().getId(), patient.get().getId());

        for (Medicine medicine : medicines)
            prescription.addMedicine(medicine);

        prescriptionsRepository.save(prescription);
    }

    public Prescription getPrescriptionByID(int prescription_id) throws PrescriptionNotFoundException {
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);
        if (prescription.isEmpty())
            throw new PrescriptionNotFoundException();

        return prescription.get();
    }

    public List<Prescription> getDoctorPrescriptions(String username) throws DoctorNotFoundException {
        Optional<User> doctor = userRepository.findByUsername(username);
        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return prescriptionsRepository.findByDoctorId(doctor.get().getId());
    }

    public List<Prescription> getUserPrescriptions(String username) throws PatientNotFoundException{
        Optional<User> patient = userRepository.findByUsername(username);
        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return prescriptionsRepository.findByPatientId(patient.get().getId());
    }

    public List<Prescription> getAllPrescriptions(){
        return StreamSupport.stream(prescriptionsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void changeValidationPrescriptions(String username, List<Integer> prescription_ids, boolean valid)
            throws PrescriptionNotFoundException, DoctorNotFoundException {
        List<Prescription> prescriptions = getDoctorPrescriptions(username);

        for (int prescription_id : prescription_ids){
            boolean exists = false;
            for (Prescription p: prescriptions){
                if (prescription_id == p.getId()){
                    exists = true;
                    p.setValid(valid);
                    prescriptionsRepository.save(p);
                    break;
                }
            }

            if (!exists)
                throw new PrescriptionNotFoundException();
        }
    }

    public void deletePrescriptions(List<Integer> prescription_ids) throws PrescriptionNotFoundException{
        for (int prescription_id : prescription_ids) {
            Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

            if (prescription.isEmpty())
                throw new PrescriptionNotFoundException();
        }
        prescriptionsRepository.deleteAllById(prescription_ids);
    }
}
