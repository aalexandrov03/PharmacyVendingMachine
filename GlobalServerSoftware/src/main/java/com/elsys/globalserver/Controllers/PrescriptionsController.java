package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.PrescriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionsController {
    private final PrescriptionsService prescriptionsService;

    @Autowired
    public PrescriptionsController(PrescriptionsService prescriptionsService) {
        this.prescriptionsService = prescriptionsService;
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPrescriptions(){
        return ResponseEntity.ok().body(prescriptionsService.getAllPrescriptions());
    }

    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatientsPrescriptions(@RequestParam int patient_id){
        try{
            return ResponseEntity.ok().body(prescriptionsService.getUserPrescriptions(patient_id));
        } catch(PatientNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctorsPrescriptions(@RequestParam int doctor_id){
        try{
            return ResponseEntity.ok().body(prescriptionsService.getDoctorPrescriptions(doctor_id));
        } catch(DoctorNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @PostMapping()
    public ResponseEntity<?> addPrescription(@RequestParam String patient_uname,
                                             @RequestParam int doctor_id,
                                             @RequestBody List<Integer> med_ids){
        try{
            prescriptionsService.addPrescription(patient_uname, doctor_id, med_ids);
            return ResponseEntity.status(201).build();
        } catch (Exception exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @DeleteMapping()
    public ResponseEntity<?> deletePrescriptions(@RequestBody List<Integer> prescription_ids){
        try{
            prescriptionsService.deletePrescriptions(prescription_ids);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @PutMapping("{action}")
    public ResponseEntity<?> changePrescriptions(@PathVariable String action,
                                                 @RequestBody List<Integer> prescription_ids){
        try{
            if (action.equals("invalidate")){
                prescriptionsService.invalidatePrescriptions(prescription_ids);
                return ResponseEntity.ok().build();

            }
            else if (action.equals("execute")){
                prescriptionsService.executePrescriptions(prescription_ids);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();
        } catch (PrescriptionNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }
}
