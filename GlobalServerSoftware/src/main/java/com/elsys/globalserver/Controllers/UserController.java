package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Models.User;
import com.elsys.globalserver.Exceptions.Users.DoctorAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UsersService usersService;

    @Autowired
    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/patient")
    public ResponseEntity<?> getPatientInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(usersService.getPatientInfo(username));
        } catch (PatientNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username =
                ((org.springframework.security.core.userdetails.User) authentication.getPrincipal()).getUsername();

        try {
            return ResponseEntity.ok().body(usersService.getDoctorInfo(username));
        } catch (DoctorNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/patient")
    public ResponseEntity<?> registerPatient(@RequestBody User patient) {
        try {
            usersService.registerPatient(patient);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (PatientAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody User doctor) {
        try {
            usersService.registerDoctor(doctor);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DoctorAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }
}
