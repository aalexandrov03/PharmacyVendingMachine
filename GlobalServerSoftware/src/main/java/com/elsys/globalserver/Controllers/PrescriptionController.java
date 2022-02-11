package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.PrescriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionsService prescriptionsService;

    @Autowired
    public PrescriptionController(PrescriptionsService prescriptionsService) {
        this.prescriptionsService = prescriptionsService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllPrescriptions(){
        return ResponseEntity.ok().body(prescriptionsService.getAllPrescriptions());
    }

    @GetMapping("/patients")
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_ADMIN')")
    public ResponseEntity<?> getPatientPrescriptions(@RequestParam int patient_id){
        try{
            return ResponseEntity.ok().body(prescriptionsService.getUserPrescriptions(patient_id));
        } catch (PatientNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getDoctorPrescriptions(@RequestParam int doctor_id){
        try{
            return ResponseEntity.ok().body(prescriptionsService.getDoctorPrescriptions(doctor_id));
        } catch (DoctorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addPrescription(@RequestParam String patient_uname,
                                             @RequestParam String doctor_uname,
                                             @RequestBody List<Integer> med_ids){
        try{
            prescriptionsService.addPrescription(patient_uname, doctor_uname, med_ids);
            return ResponseEntity.status(201).build();
        } catch (Exception exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePrescriptions(@RequestBody List<Integer> prescription_ids){
        try{
            prescriptionsService.deletePrescriptions(prescription_ids);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @PutMapping("/invalidate")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<?> changeValidationPrescriptions(@RequestBody List<Integer> prescription_ids,
                                                           @RequestParam boolean valid){
        try{
            prescriptionsService.changeValidationPrescriptions(prescription_ids, valid);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }
}
