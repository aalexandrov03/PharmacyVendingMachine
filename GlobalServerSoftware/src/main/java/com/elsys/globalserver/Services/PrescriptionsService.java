package com.elsys.globalserver.Services;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.DataAccess.PrescriptionRepository;
import com.elsys.globalserver.DataAccess.UserRepository;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Prescription;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Models.User;
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

    public void addPrescription(String patient_email, String doctor_email, List<MedOrder> medicines)
            throws MedicineNotFoundException, PatientNotFoundException, DoctorNotFoundException{
        Optional<User> patient = userRepository.findUserByEmail(patient_email);
        Optional<User> doctor = userRepository.findUserByEmail(doctor_email);
        List<Medicine> meds = new ArrayList<>();

        for (MedOrder medOrder : medicines){
            Optional<Medicine> medicine = medicinesRepository.findByName(medOrder.getName());

            if (medicine.isEmpty())
                throw new MedicineNotFoundException();

            for (int i = 0; i < medOrder.getAmount(); i ++)
                meds.add(medicine.get());
        }

        if (patient.isEmpty())
            throw new PatientNotFoundException();
        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        Prescription prescription = new Prescription(doctor_email, patient_email);
        prescription.setMedicines(meds);

        prescriptionsRepository.save(prescription);
    }

    public Prescription getPrescriptionByID(int prescription_id) throws PrescriptionNotFoundException {
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);
        if (prescription.isEmpty())
            throw new PrescriptionNotFoundException();

        return prescription.get();
    }

    public List<Prescription> getDoctorPrescriptions(String email) throws DoctorNotFoundException {
        Optional<User> doctor = userRepository.findUserByEmail(email);
        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return prescriptionsRepository.findByDoctor(doctor.get().getEmail());
    }

    public List<Prescription> getUserPrescriptions(String email) throws PatientNotFoundException{
        Optional<User> patient = userRepository.findUserByEmail(email);
        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return prescriptionsRepository.findByPatient(patient.get().getEmail());
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
