package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.Services.Microservices.AuthenticationService;
import com.elsys.globalserver.Services.Microservices.BugService;
import com.elsys.globalserver.Services.Microservices.MedicineService;
import com.elsys.globalserver.Services.Microservices.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DoctorsService {
    private final AuthenticationService authenticationService;
    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;
    private final BugService bugService;

    @Autowired
    public DoctorsService(AuthenticationService authenticationService,
                          PrescriptionService prescriptionService,
                          MedicineService medicineService,
                          BugService bugService) {
        this.authenticationService = authenticationService;
        this.prescriptionService = prescriptionService;
        this.medicineService = medicineService;
        this.bugService = bugService;
    }

    public boolean register(Doctor doctor_data) {
        return authenticationService.registerDoctor(doctor_data);
    }

    public Optional<Doctor> login(String username, String password) {
        return authenticationService.loginDoctor(username, password);
    }

    public List<Medicine> getAllMedicines() {
        return medicineService.getMedicines();
    }

    public boolean addPrescription(String username, int doctor_id, List<Integer> med_ids) {
        return prescriptionService.addPrescription(username, doctor_id, med_ids);
    }

    public Set<Prescription> getDoctorPrescriptions(int doctor_id) {
        return prescriptionService.getDoctorPrescriptions(doctor_id);
    }

    public void reportBug(Bug bug) {
        bugService.reportBug(bug);
    }

    public boolean invalidatePrescription(int prescription_id){
        return prescriptionService.invalidatePrescription(prescription_id);
    }
}
