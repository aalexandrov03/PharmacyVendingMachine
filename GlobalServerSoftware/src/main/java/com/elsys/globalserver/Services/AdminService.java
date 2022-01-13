package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.Services.Microservices.BugService;
import com.elsys.globalserver.Services.Microservices.MedicineService;
import com.elsys.globalserver.Services.Microservices.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final MedicineService medicineService;
    private final PrescriptionService prescriptionService;
    private final BugService bugService;

    @Autowired
    public AdminService(MedicineService medicineService,
                        PrescriptionService prescriptionService,
                        BugService bugService){
        this.medicineService = medicineService;
        this.prescriptionService = prescriptionService;
        this.bugService = bugService;
    }

    public boolean addMedicine(Medicine medicine) {
        return medicineService.addMedicine(medicine);
    }

    public boolean deletePrescription(int prescription_id){
        return prescriptionService.deletePrescription(prescription_id);
    }

    public boolean deleteMedicine(int medicine_id) {
        return medicineService.deleteMedicine(medicine_id);
    }

    public List<Medicine> getMedicines() {
        return medicineService.getMedicines();
    }

    public List<Bug> getBugs() {
        return bugService.getBugs();
    }

    public boolean clearBug(int bug_id) {
        return bugService.clearBug(bug_id);
    }
}
