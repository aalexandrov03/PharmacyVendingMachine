package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.PrescriptionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prescriptions")
@CrossOrigin(origins = "*")
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

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR')")
    public ResponseEntity<?> getPrescriptions(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();

        try{
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("PATIENT")))
                return ResponseEntity.ok().body(prescriptionsService.getUserPrescriptions(username));
            else
                return ResponseEntity.ok().body(prescriptionsService.getDoctorPrescriptions(username));
        } catch (PatientNotFoundException | DoctorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("{prescription_id}")
    @PreAuthorize("hasRole('ROLE_MACHINE')")
    public ResponseEntity<?> getPrescriptionByID(@PathVariable int prescription_id){
        try{
            return ResponseEntity.ok().body(prescriptionsService.getPrescriptionByID(prescription_id));
        } catch (PrescriptionNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping()
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> addPrescription(@RequestParam String patient_uname,
                                             @RequestBody List<Integer> med_ids){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();

        try{
            prescriptionsService.addPrescription(patient_uname, username, med_ids);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = ((User) authentication.getPrincipal()).getUsername();

        try{
            prescriptionsService.changeValidationPrescriptions(username, prescription_ids, valid);
            return ResponseEntity.ok().build();
        } catch (PrescriptionNotFoundException | DoctorNotFoundException exception){
            return ResponseEntity.status(404).body(exception.getMessage());
        }
    }
}
