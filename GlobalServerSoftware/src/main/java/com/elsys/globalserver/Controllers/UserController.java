package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Exceptions.Users.DoctorAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Patient;
import com.elsys.globalserver.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/patient")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> getPatientInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(userService.getPatientInfo(email));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> getDoctorInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();
        
        try {
            return ResponseEntity.ok().body(userService.getDoctorInfo(email));
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/patient")
    public ResponseEntity<?> registerPatient(@RequestBody Patient patient) {
        try {
            userService.registerPatient(patient);
            return ResponseEntity.ok().build();
        } catch (PatientAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody Doctor doctor) {
        try {
            userService.registerDoctor(doctor);
            return ResponseEntity.ok().build();
        } catch (DoctorAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
