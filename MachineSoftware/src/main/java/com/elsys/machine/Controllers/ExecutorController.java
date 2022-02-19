package com.elsys.machine.Controllers;

import com.elsys.machine.Controllers.Utils.PrescriptionDTO;
import com.elsys.machine.Services.ExecutorService;
import com.elsys.machine.Services.Utils.ValidationResult;
import com.elsys.machine.Services.Utils.ValidationResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
        try {
            ValidationResult result = executorService.executePrescription(prescriptionDTO);
            return ResponseEntity.ok().body(
                    new ValidationResultDTO(result.getStatus(), result.getMessage())
                    );
        }catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred!");
        }
    }
}
