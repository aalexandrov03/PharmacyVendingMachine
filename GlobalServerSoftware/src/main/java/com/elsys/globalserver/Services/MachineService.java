package com.elsys.globalserver.Services;

import com.elsys.globalserver.Services.Microservices.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MachineService {
    private final PrescriptionService prescriptionService;

    @Autowired
    public MachineService(PrescriptionService prescriptionService){
        this.prescriptionService = prescriptionService;
    }

    public boolean invalidatePrescription(int prescription_id){
        return prescriptionService.invalidatePrescription(prescription_id);
    }
}
