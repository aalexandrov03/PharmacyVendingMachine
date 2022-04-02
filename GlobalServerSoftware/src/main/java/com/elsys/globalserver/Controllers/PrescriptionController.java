package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
public class PrescriptionController {
    private final PrescriptionService prescriptionService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllPrescriptions() {
        return ResponseEntity.ok().body(prescriptionService.getAllPrescriptions());
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> getPatientPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(prescriptionService.getPatientPrescriptions(email));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getDoctorPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(prescriptionService.getDoctorPrescriptions(email));
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/machine/{prescription_id}")
    @PreAuthorize("hasRole('ROLE_MACHINE')")
    public ResponseEntity<?> getPrescriptionByID(@PathVariable int prescription_id) {
        try {
            return ResponseEntity.ok().body(prescriptionService.getPrescriptionByID(prescription_id));
        } catch (PrescriptionNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addPrescription(@RequestParam String patient_email, @RequestBody List<MedOrder> medicines) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            prescriptionService.addPrescription(patient_email, email, medicines);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePrescription(@RequestParam int id) {
        try {
            prescriptionService.deletePrescription(id);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('ROLE_DOCTOR')")
    public ResponseEntity<?> changeValidationPrescriptions(@RequestParam int id,
                                                           @RequestParam boolean valid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            prescriptionService.changeValidationPrescriptions(email, id, valid);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }
}
