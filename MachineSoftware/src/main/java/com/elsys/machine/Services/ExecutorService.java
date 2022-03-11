package com.elsys.machine.Services;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Utils.RouteNode;
import com.elsys.machine.Controllers.Utils.Prescription;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

    public Optional<Prescription> getPrescriptionFromServer(int prescription_id) throws UnirestException, IOException {
        HttpResponse<Prescription> response = Unirest.get(configurationService.getServerAddress()
                        + "/prescriptions/{prescription_id}")
                .routeParam("prescription_id", String.valueOf(prescription_id))
                .basicAuth("machine", "machine").asObject(Prescription.class);

        return Optional.of(response.getBody());
    }

    private ValidationResult checkPrescription(Prescription prescription) {
        List<Medicine> medicines = medicineService.getMedicines("both");

        if (!prescription.isValid())
            return INVALID;

        int count = 0;
        for (Medicine medicine : prescription.getMedicines()) {
            for (Medicine m : medicines) {
                if (medicine.equals(m))
                    if (m.getAmount() >= medicine.getAmount())
                        count++;
            }
        }

        if (count == prescription.getMedicines().size())
            return OK;
        else
            return NOT_AVAILABLE;
    }

    public ValidationResult executePrescription(Prescription prescription) throws Exception {
        ValidationResult status = checkPrescription(prescription);

        switch(status){
            case OK:
                if (!configurationService.getStatus())
                    return SHUTDOWN;

                Router router = new Router(configurationService.getConfiguration());
                List<RouteNode> route = router.createRoute(prescription.getMedicines());

                synchronized (Executor.getExecutor()){
                    Executor.getExecutor().execute(route);
                }

                List<Medicine> medicines = medicineService.getMedicines("both");
                for (Medicine medicine : medicines)
                    for (Medicine medicine1 : prescription.getMedicines())
                        if (medicine1.equals(medicine)) {
                            medicine.setAmount(medicine.getAmount() - medicine1.getAmount());
                            medicineService.updateMedicine(medicine.getName(), medicine);
                            break;
                        }

            case INVALID:
            case NOT_AVAILABLE:
            default:
                return status;
        }
    }
}
