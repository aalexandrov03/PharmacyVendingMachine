package com.elsys.globalserver.Services;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.DataAccess.PrescriptionRepository;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Patient;
import com.elsys.globalserver.Models.Prescription;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.Utils.PrescriptionDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptionsRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final MedicineRepository medicinesRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionsRepository,
                               PatientRepository patientRepository,
                               DoctorRepository doctorRepository,
                               MedicineRepository medicinesRepository) {
        this.prescriptionsRepository = prescriptionsRepository;
        this.medicinesRepository = medicinesRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public void addPrescription(String patient_email, String doctor_email, List<MedOrder> medicines)
            throws MedicineNotFoundException, PatientNotFoundException {
        Optional<Patient> patient = patientRepository.findByEmail(patient_email);

        if (patient.isEmpty())
            throw new PatientNotFoundException();

        List<Medicine> meds = new ArrayList<>();

        for (MedOrder medOrder : medicines) {
            Optional<Medicine> medicine = medicinesRepository.findByName(medOrder.getName());

            if (medicine.isEmpty())
                throw new MedicineNotFoundException();

            for (int i = 0; i < medOrder.getAmount(); i++)
                meds.add(medicine.get());
        }

        Prescription prescription = new Prescription(doctor_email, patient_email);
        prescription.setMedicines(meds);

        prescriptionsRepository.save(prescription);
    }

    public PrescriptionDTO getPrescriptionByID(int prescription_id) throws PrescriptionNotFoundException {
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);
        if (prescription.isEmpty())
            throw new PrescriptionNotFoundException();

        return new PrescriptionDTO(prescription.get());
    }

    public List<PrescriptionDTO> getDoctorPrescriptions(String email) throws DoctorNotFoundException {
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);

        if (doctor.isEmpty())
            throw new DoctorNotFoundException();

        return prescriptionsRepository.findByDoctor(doctor.get().getEmail())
                .stream().map(PrescriptionDTO::new)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getPatientPrescriptions(String email) throws PatientNotFoundException {
        Optional<Patient> patient = patientRepository.findByEmail(email);

        if (patient.isEmpty())
            throw new PatientNotFoundException();

        return prescriptionsRepository.findByPatient(patient.get().getEmail())
                .stream().map(PrescriptionDTO::new)
                .collect(Collectors.toList());
    }

    public List<PrescriptionDTO> getAllPrescriptions() {
        return StreamSupport.stream(prescriptionsRepository.findAll().spliterator(), false)
                .map(PrescriptionDTO::new)
                .collect(Collectors.toList());
    }

    public void changeValidationPrescriptions(String email, int prescription_id, boolean valid)
            throws PrescriptionNotFoundException{
        List<Prescription> prescriptions = prescriptionsRepository.findByDoctor(email);

        boolean exists = false;
        for (Prescription prescription : prescriptions){
            if (prescription.getId() == prescription_id){
                exists = true;
                prescription.setValid(valid);
                prescriptionsRepository.save(prescription);
                break;
            }
        }

        if (!exists)
            throw new PrescriptionNotFoundException();
    }

    public void deletePrescription(int prescription_id) throws PrescriptionNotFoundException {
        Optional<Prescription> prescription = prescriptionsRepository.findById(prescription_id);

        if (prescription.isEmpty())
            throw new PrescriptionNotFoundException();

        prescriptionsRepository.deleteById(prescription_id);
    }
}
