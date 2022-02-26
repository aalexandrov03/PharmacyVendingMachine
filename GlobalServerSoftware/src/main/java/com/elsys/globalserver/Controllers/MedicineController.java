package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Services.MedicinesService;
import com.elsys.globalserver.Exceptions.Medicines.MedicineAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicinesService medicinesService;

    @Autowired
    public MedicineController(MedicinesService medicinesService) {
        this.medicinesService = medicinesService;
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<?> getMedicines(){
        return ResponseEntity.ok().body(medicinesService.getMedicines());
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addMedicines(@RequestBody List<Medicine> medicines){
        try{
            medicinesService.addMedicines(medicines);
            return ResponseEntity.status(201).build();
        } catch (MedicineAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMedicines(@RequestBody List<Integer> medicine_ids){
        try{
            medicinesService.deleteMedicines(medicine_ids);
            return ResponseEntity.ok().build();
        } catch (MedicineNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
