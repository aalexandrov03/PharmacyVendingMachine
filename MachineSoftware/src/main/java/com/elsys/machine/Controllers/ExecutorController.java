package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.Prescription;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.ExecutorService;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.elsys.machine.Services.Utils.ValidationResultDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/executor")
public class ExecutorController {
    private final ExecutorService executorService;

    @Autowired
    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostMapping()
    public ResponseEntity<?> executeOrder(@RequestParam boolean fetch,
                                          @RequestBody(required = false) Set<Medicine> order,
                                          @RequestParam(required = false) Integer id) {
        if (fetch) {
            if (id != null) {
                try {
                    Optional<Prescription> prescription = executorService.getPrescriptionFromServer(id);

                    if (prescription.isEmpty())
                        return ResponseEntity.internalServerError().body("Prescription does not exist in the server!");

                    ValidationResult result = executorService.executePrescription(prescription.get());
                    return ResponseEntity.ok().body(
                            new ValidationResultDTO(result.getStatus(), result.getMessage())
                    );
                } catch (UnirestException e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError()
                            .body("An unexpected error occurred while fetching the prescription!");
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Unexpected error occurred!");
                }
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else {
            if (order != null) {
                try {
                    ValidationResult result = executorService.executePrescription(
                            new Prescription(true, order)
                    );
                    return ResponseEntity.ok().body(
                            new ValidationResultDTO(result.getStatus(), result.getMessage())
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return ResponseEntity.internalServerError().body("Unexpected error occurred!");
                }
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
