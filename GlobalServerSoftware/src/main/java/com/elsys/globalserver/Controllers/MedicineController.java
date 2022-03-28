package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Exceptions.Medicines.MedicineAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Services.MedicineService;
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<?> getMedicines(){
        return ResponseEntity.ok().body(medicineService.getMedicines());
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine){
        try{
            medicineService.addMedicine(medicine);
            return ResponseEntity.ok().build();
        } catch (MedicineAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteMedicine(@RequestParam String name){
        try{
            medicineService.deleteMedicine(name);
            return ResponseEntity.ok().build();
        } catch (MedicineNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateMedicine(@RequestParam String name,
                                            @RequestBody Medicine medicine){
        try{
            medicineService.updateMedicine(name, medicine);
            return ResponseEntity.ok().build();
        } catch (MedicineNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
