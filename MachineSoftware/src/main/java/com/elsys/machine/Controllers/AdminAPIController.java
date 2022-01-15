package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.ReloadRequest;
import com.elsys.machine.Services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/status/history")
    public ResponseEntity<?> getStatusHistory() {
        return ResponseEntity.ok().body(adminService.getStatusHistory());
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLastStatus() {
        return ResponseEntity.ok().body(adminService.getStatus());
    }

    @PostMapping("/status")
    public ResponseEntity<?> setStatus(@RequestBody boolean status) {
        adminService.setStatus(status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/status/history")
    public ResponseEntity<?> clearStatusHistory() {
        adminService.clearStatusHistory();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines() {
        return ResponseEntity.ok().body(adminService.getAllMedicines());
    }

    @GetMapping("/routing")
    public ResponseEntity<?> getRouting() {
        return ResponseEntity.ok().body(adminService.getRouting());
    }

    @GetMapping("/reload/history")
    public ResponseEntity<?> getReloadsHistory() {
        return ResponseEntity.ok().body(adminService.getReloadHistory());
    }

    @DeleteMapping("/reload/history")
    public ResponseEntity<?> clearReloadsHistory() {
        adminService.clearReloadHistory();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reload")
    public ResponseEntity<?> reloadMachine(@RequestBody ReloadRequest request) {
        adminService.reload(request.getMedicines(), request.getRouting());
        return ResponseEntity.ok().build();
    }
}
