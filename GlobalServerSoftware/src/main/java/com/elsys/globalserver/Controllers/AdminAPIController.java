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

    @GetMapping("/getMedicines")
    public ResponseEntity<?> getMedicines() {
        return ResponseEntity.ok().body(adminService.getMedicines());
    }

    @GetMapping("/getBugs")
    public ResponseEntity<?> getBugs() {
        return ResponseEntity.ok().body(adminService.getBugs());
    }

    @PostMapping("/addMedicine")
    public ResponseEntity<?> addMedicine(@RequestBody Medicine medicine) {
        boolean added = adminService.addMedicine(medicine);

        if (!added)
            return ResponseEntity.status(HttpStatus.FOUND).build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deleteMedicine")
    public ResponseEntity<?> deleteMedicine(@RequestHeader(name = "MED_ID") int medicine_id) {
        boolean deleted = adminService.deleteMedicine(medicine_id);

        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clearBug")
    public ResponseEntity<?> clearBug(@RequestHeader(name = "BUG_ID") int bug_id) {
        boolean deleted = adminService.clearBug(bug_id);

        if (!deleted)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }
}
