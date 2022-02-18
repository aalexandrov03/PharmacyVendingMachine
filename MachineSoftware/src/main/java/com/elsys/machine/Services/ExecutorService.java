package com.elsys.machine.Services;

import com.elsys.machine.Control.MachineDriver;
import com.elsys.machine.Controllers.Utils.PrescriptionDTO;
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

    public VALIDATION_STATUS checkPrescription(PrescriptionDTO prescriptionDTO) {
        List<Medicine> medicines = medicineService.getMedicines("both");

        if (!prescriptionDTO.isValid())
            return INVALID;

        int count = 0;
        for (Medicine medicine : prescriptionDTO.getMedicines()) {
            for (Medicine m : medicines) {
                if (medicine.equals(m))
                    if (m.getAmount() >= medicine.getAmount())
                        count++;
            }
        }

        if (count == 0)
            return NOT_AVAILABLE;
        else if (count == prescriptionDTO.getMedicines().size())
            return ALL_AVAILABLE;
        else
            return PARTLY_AVAILABLE;
    }

    public void executePrescription(PrescriptionDTO prescriptionDTO) {
        machineDriver.execute(prescriptionDTO.getMedicines());
    }
}
