package com.elsys.machine.Controllers;

import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.Services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/casual/users/api")
public class CasualUsersAPIController {
    private final OrderService orderService;

    @Autowired
    public CasualUsersAPIController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getMachineStatus() {
        return ResponseEntity.ok().body(orderService.getStatus());
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicinesWithoutPrescription() {
        return ResponseEntity.ok().body(orderService.getNoPrescriptionMedicines());
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validatePrescription(@RequestBody List<Medicine> prescription) {
        int validation = orderService.validatePrescription(prescription);
        return ResponseEntity.ok().body(validation);
    }

    @PostMapping("/execute")
    public ResponseEntity<?> executePrescription(@RequestBody List<Medicine> prescription) {
        boolean status = orderService.executePrescription(prescription);

        if (!status)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }
}
