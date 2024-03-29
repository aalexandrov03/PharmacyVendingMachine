package com.elsys.machine.Services;

import com.elsys.machine.Control.Driver.Executor;
import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Utils.RouteNode;
import com.elsys.machine.Controllers.Utils.Prescription;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.Utils.MedicineQuantity;
import com.elsys.machine.Services.Utils.PrescriptionDTO;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public Optional<Prescription> getPrescriptionFromServer(int prescription_id)
            throws UnirestException, IOException {
        HttpResponse<JsonNode> response = Unirest.get(configurationService.getServerAddress()
                        + "/prescriptions/machine/{prescription_id}")
                .basicAuth("machine", "machine")
                .routeParam("prescription_id", String.valueOf(prescription_id))
                .asJson();

        if (response.getStatus() == 404)
            return Optional.empty();

        PrescriptionDTO prescriptionDTO = new Gson().fromJson(response.getBody().toString(), PrescriptionDTO.class);
        Prescription prescription = new Prescription();
        prescription.setValid(prescriptionDTO.isValid());
        prescription.setMedicines(prescriptionDTO.getMedicines()
                .stream().collect(Collectors.toMap(
                        medicine -> medicineService.getMedicineByName(medicine.getName()),
                        MedicineQuantity::getQuantity
                )));

        return Optional.of(prescription);
    }

    private ValidationResult checkPrescription(Prescription prescription, boolean fetch)
            throws IllegalArgumentException {
        List<Medicine> medicines = medicineService.getMedicines(fetch ? "both" : "no");

        if (!prescription.isValid())
            return INVALID;

        int count = 0;
        for (Medicine medicine : prescription.getMedicines().keySet()) {
            if (!medicines.contains(medicine))
                break;

            for (Medicine medicine1 : medicines) {
                if (medicine1.equals(medicine)){
                    if (medicine1.getAmount() >= prescription.getMedicines().get(medicine))
                        count ++;
                }
            }
        }

        if (count != prescription.getMedicines().size())
            return NOT_AVAILABLE;

        List<String> medNames = configurationService.getMapping().stream()
                .map(Mapping::getMedicineName).collect(Collectors.toList());

        for (Medicine medicine : prescription.getMedicines().keySet())
            if (!medNames.contains(medicine.getName()))
                return NO_MAPPING;

        return OK;
    }

    public ValidationResult executePrescription(Prescription prescription, boolean fetch)
            throws IOException, IllegalArgumentException {
        ValidationResult status = checkPrescription(prescription, fetch);

        if (status == OK) {
            if (!configurationService.getStatus())
                return SHUTDOWN;

            Router router = new Router(
                    configurationService.getRouterSettings(),
                    configurationService.getMapping()
            );

            List<RouteNode> route = router.createRoute(prescription.getMedicines());

            synchronized (Executor.getExecutor()) {
                Executor.getExecutor().execute(route);
            }

            Map<Medicine, Integer> medicines = prescription.getMedicines();

            for (Medicine medicine : medicines.keySet()){
                medicine.setAmount(medicine.getAmount() - medicines.get(medicine));
                medicineService.updateMedicine(medicine.getName(), medicine);
            }
        }

        return status;
    }
}
