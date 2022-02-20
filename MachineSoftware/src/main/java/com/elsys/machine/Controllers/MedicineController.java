package com.elsys.machine.Controllers;

import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Services.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping()
    public ResponseEntity<?> getMedicines(@RequestParam String prescription){
        try{
            return ResponseEntity.ok().body(medicineService.getMedicines(prescription));
        } catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine){
        try{
            medicineService.addMedicine(medicine);
            return ResponseEntity.status(201).build();
        } catch(Exception e){
            return ResponseEntity.status(302).body(e.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMedicine(@RequestParam String name){
        try{
            medicineService.deleteMedicine(name);
            return ResponseEntity.status(200).build();
        } catch(Exception e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateMedicine(@RequestBody Medicine medicine,
                                            @RequestParam String name){
        try{
            medicineService.updateMedicine(name, medicine);
            return ResponseEntity.ok().build();
        } catch(Exception e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
