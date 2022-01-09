package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/api")
public class AdminAPIController {
    private final AdminService adminService;

    @Autowired
    public AdminAPIController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines() {
        return ResponseEntity.ok().body(adminService.getMedicines());
    }

    @GetMapping("/bugs")
    public ResponseEntity<?> getBugs() {
        return ResponseEntity.ok().body(adminService.getBugs());
    }

    @PostMapping("/medicine")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        boolean added = adminService.addMedicine(medicine);

        if (!added)
            return ResponseEntity.status(HttpStatus.FOUND).build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/prescription")
    public ResponseEntity<?> deletePrescription(@RequestParam int prescription_id){
        boolean deleted = adminService.deletePrescription(prescription_id);

        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/medicine")
    public ResponseEntity<?> deleteMedicine(@RequestParam int medicine_id) {
        boolean deleted = adminService.deleteMedicine(medicine_id);

        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bug")
    public ResponseEntity<?> clearBug(@RequestParam int bug_id) {
        boolean deleted = adminService.clearBug(bug_id);

        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }
}