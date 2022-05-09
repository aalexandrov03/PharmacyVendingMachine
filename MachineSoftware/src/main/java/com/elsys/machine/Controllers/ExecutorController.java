package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.MedOrder;
import com.elsys.machine.Controllers.Utils.Prescription;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Exceptions.MedicineNotFoundException;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.ExecutorService;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.elsys.machine.Services.Utils.ValidationResultDTO;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/executor")
public class ExecutorController {
    private final ExecutorService executorService;
    private final MedicinesRepository medicinesRepository;

    @Autowired
    public ExecutorController(ExecutorService executorService,
                              MedicinesRepository medicinesRepository) {
        this.executorService = executorService;
        this.medicinesRepository = medicinesRepository;
    }

    @PostMapping("/order")
    public ResponseEntity<?> executeOrder(@RequestBody Set<MedOrder> order) {
        try {
            Set<Medicine> medicineSet = order.stream().map(medOrder -> {
                Optional<Medicine> medicine = medicinesRepository
                        .findMedicineByName(medOrder.getName());

                if (medicine.isEmpty())
                    throw new MedicineNotFoundException(medOrder.getName());

                return medicine.get();
            }).collect(Collectors.toSet());

            ValidationResult result = executorService.executePrescription(
                    new Prescription(true, medicineSet), false
            );
            return ResponseEntity.ok().body(
                    new ValidationResultDTO(result.getStatus(), result.getMessage())
            );
        } catch (MedicineNotFoundException noMedicine){
            return ResponseEntity.status(404).body(noMedicine.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Unexpected error occurred!");
        }
    }

    @PostMapping("/fetch")
    public ResponseEntity<?> executePrescription(@RequestBody int id) {
        try {
            Optional<Prescription> prescription = executorService.getPrescriptionFromServer(id);

            if (prescription.isEmpty())
                return ResponseEntity.internalServerError().body("Prescription does not exist in the server!");

            ValidationResult result = executorService.executePrescription(prescription.get(), true);
            return ResponseEntity.ok().body(
                    new ValidationResultDTO(result.getStatus(), result.getMessage())
            );
        } catch (UnirestException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("An unexpected error occurred while fetching the prescription!");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Unexpected error occurred!");
        }
    }
}
