package com.elsys.machine.Services;

import com.elsys.machine.Control.MachineDriver;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.Utils.VALIDATION_STATUS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.elsys.machine.Services.Utils.VALIDATION_STATUS.*;

@Service
public class ExecutorService {
    private final MachineDriver machineDriver;
    private final MedicineService medicineService;

    @Autowired
    public ExecutorService(MachineDriver machineDriver,
                           MedicineService medicineService) {
        this.machineDriver = machineDriver;
        this.medicineService = medicineService;
    }

    public VALIDATION_STATUS checkPrescription(List<Medicine> prescription) {
        List<Medicine> medicines = medicineService.getMedicines("both");

        int count = 0;
        for (Medicine medicine : prescription) {
            for (Medicine m : medicines) {
                if (medicine.equals(m))
                    if (m.getAmount() >= medicine.getAmount())
                        count++;
            }
        }

        if (count == 0)
            return NOT_AVAILABLE;
        else if (count == prescription.size())
            return ALL_AVAILABLE;
        else
            return PARTLY_AVAILABLE;
    }

    public void executePrescription(List<Medicine> prescription) {
        machineDriver.execute(prescription);
    }
}
