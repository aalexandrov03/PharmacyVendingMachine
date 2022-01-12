package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.ReloadRequest;
import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.Services.AdminService.ReloadService;
import com.elsys.machine.Services.AdminService.StatusService;
import com.elsys.machine.Services.AdminService.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
public class AdminAPIController {
    private final StatusService statusService;
    private final TestService testService;
    private final ReloadService reloadService;

    @Autowired
    public AdminAPIController(StatusService statusService,
                              TestService testService,
                              ReloadService reloadService) {
        this.statusService = statusService;
        this.testService = testService;
        this.reloadService = reloadService;
    }

    @GetMapping("/status/history")
    public ResponseEntity<?> getStatusHistory(){
        return ResponseEntity.ok().body(statusService.getStatusHistory());
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLastStatus(){
        return ResponseEntity.ok().body(statusService.getLastStatus());
    }

    @PostMapping("/status")
    public ResponseEntity<?> setStatus(@RequestBody boolean status){
        statusService.setStatus(status);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/status/history")
    public ResponseEntity<?> clearStatusHistory(){
        statusService.clearStatusHistory();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines(){
        return ResponseEntity.ok().body(reloadService.getCurrentMedicines());
    }

    @GetMapping("/routing")
    public ResponseEntity<?> getRouting(){
        return ResponseEntity.ok().body(reloadService.getCurrentRouting());
    }

    @GetMapping("/reload/history")
    public ResponseEntity<?> getReloadsHistory(){
        return ResponseEntity.ok().body(reloadService.getReloadsHistory());
    }

    @DeleteMapping("/reload/history")
    public ResponseEntity<?> clearReloadsHistory(){
        reloadService.clearReloadHistory();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reload")
    public ResponseEntity<?> reloadMachine(@RequestBody ReloadRequest request){
        reloadService.reloadMachine(request.getMedicines(), request.getRouting());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/test/history")
    public ResponseEntity<?> getTestsHistory(){
        return ResponseEntity.ok().body(testService.getTestsHistory());
    }

    @DeleteMapping("/test/history")
    public ResponseEntity<?> clearTestsHistory(){
        testService.clearTestsHistory();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<?> executeTestPrescription(@RequestBody List<Medicine> test_prescription){
        testService.executeTestPrescription(test_prescription);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitResult(@RequestBody boolean result){
        testService.submitResult(result);
        return ResponseEntity.ok().build();
    }
}
