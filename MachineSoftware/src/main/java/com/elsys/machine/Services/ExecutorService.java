package com.elsys.machine.Services;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Utils.RouteNode;
import com.elsys.machine.Controllers.Utils.PrescriptionDTO;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.Utils.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.elsys.machine.Services.Utils.ValidationResult.*;

@Service
public class ExecutorService {
    private final MedicineService medicineService;
    private final ConfigurationService configurationService;

    @Autowired
    public ExecutorService(MedicineService medicineService,
                           ConfigurationService configurationService) {
        this.medicineService = medicineService;
        this.configurationService = configurationService;
    }

    private ValidationResult checkPrescription(PrescriptionDTO prescriptionDTO) {
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
            return OK;
        else
            return PARTLY_AVAILABLE;
    }

    public ValidationResult executePrescription(PrescriptionDTO prescriptionDTO) throws IOException {
        ValidationResult status = checkPrescription(prescriptionDTO);

        switch(status){
            case OK:
                if (!configurationService.getStatus())
                    return SHUTDOWN;

                Router router = new Router(configurationService.getConfiguration());
                List<RouteNode> route = router.createRoute(prescriptionDTO.getMedicines());

                synchronized (Executor.getExecutor()){
                    Executor.getExecutor().execute(route);
                }

            case INVALID:
            case PARTLY_AVAILABLE:
            case NOT_AVAILABLE:
            default:
                return status;
        }
    }
}
