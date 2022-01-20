package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Services.MachineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machine/api")
public class MachineAPIController {
    private final MachineService machineService;

    @Autowired
    public MachineAPIController(MachineService machineService){
        this.machineService = machineService;
    }

    @PutMapping("/invalidate")
    public ResponseEntity<?> invalidatePrescription(@RequestParam int prescription_id){
        boolean status = machineService.invalidatePrescription(prescription_id);

        if (!status)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }
}
