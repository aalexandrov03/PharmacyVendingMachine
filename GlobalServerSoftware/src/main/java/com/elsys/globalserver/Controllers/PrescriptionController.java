package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.PrescriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
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
    public ResponseEntity<?> getAllPrescriptions() {
        return ResponseEntity.ok().body(prescriptionsService.getAllPrescriptions());
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> getPatientPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(prescriptionsService.getUserPrescriptions(email));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getDoctorPrescriptions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = ((User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(prescriptionsService.getDoctorPrescriptions(email));
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/machine/{prescription_id}")
    @PreAuthorize("hasRole('ROLE_MACHINE')")
    public ResponseEntity<?> getPrescriptionByID(@PathVariable int prescription_id) {
        try {
            return ResponseEntity.ok().body(prescriptionsService.getPrescriptionByID(prescription_id));
        } catch (PrescriptionNotFoundException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addPrescription(@RequestParam String patient_email, @RequestBody List<MedOrder> medicines) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();

        try {
            prescriptionsService.addPrescription(patient_email, username, medicines);
            return ResponseEntity.ok().build();
        } catch (Exception exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deletePrescriptions(@RequestBody List<Integer> prescription_ids) {
        try {
            prescriptionsService.deletePrescriptions(prescription_ids);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public ResponseEntity<?> changeValidationPrescriptions(@RequestBody List<Integer> prescription_ids, @RequestParam boolean valid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();

        try {
            prescriptionsService.changeValidationPrescriptions(username, prescription_ids, valid);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException | DoctorNotFoundException exception) {
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }
}
