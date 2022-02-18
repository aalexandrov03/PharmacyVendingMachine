package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.PrescriptionDTO;
import com.elsys.machine.Services.ExecutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/executor")
public class ExecutorController {
    private final ExecutorService executorService;

    @Autowired
    public ExecutorController(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @PostMapping()
    public ResponseEntity<?> executePrescription(@RequestBody PrescriptionDTO prescriptionDTO){
        executorService.executePrescription(prescriptionDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<?> validatePrescription(@RequestBody PrescriptionDTO prescriptionDTO){
        return ResponseEntity.ok().body(executorService.checkPrescription(prescriptionDTO));
    }
}
