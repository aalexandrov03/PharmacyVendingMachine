package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.Prescription;
import com.elsys.machine.Services.ExecutorService;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.elsys.machine.Services.Utils.ValidationResultDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/executor")
public class ExecutorController {
    private final ExecutorService executorService;

    @Autowired
    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostMapping()
    public ResponseEntity<?> executePrescription(@RequestParam int prescriptionId){
        try {
            Optional<Prescription> prescription = executorService.getPrescriptionFromServer(prescriptionId);

            if (prescription.isEmpty())
                return ResponseEntity.internalServerError().body("Prescription does not exist in the server!");

            ValidationResult result = executorService.executePrescription(prescription.get());
            return ResponseEntity.ok().body(
                    new ValidationResultDTO(result.getStatus(), result.getMessage())
                );
        }catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Unexpected error occurred!");
        } catch (UnirestException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("An unexpected error occurred while fetching the prescription!");
        }
    }
}
