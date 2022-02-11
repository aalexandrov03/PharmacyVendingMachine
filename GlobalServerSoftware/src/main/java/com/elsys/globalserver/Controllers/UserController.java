package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DatabaseEntities.User;
import com.elsys.globalserver.Exceptions.Users.*;
import com.elsys.globalserver.Services.UsersService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getPatientInfo(@RequestHeader("Authorization") String encoded_credentials){
        encoded_credentials = encoded_credentials.split(" ")[1];
        String decoded_credentials = new String(Base64.decodeBase64(encoded_credentials));

        try{
            return ResponseEntity.ok().body(usersService.getPatientInfo(decoded_credentials.split(":")[0]));
        } catch (PatientNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/doctor")
    public ResponseEntity<?> getDoctorInfo(@RequestHeader("Authorization") String encoded_credentials){
        encoded_credentials = encoded_credentials.split(" ")[1];
        String decoded_credentials = new String(Base64.decodeBase64(encoded_credentials));

        try{
            return ResponseEntity.ok().body(usersService.getDoctorInfo(decoded_credentials.split(":")[0]));
        } catch (DoctorNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<?> getAdminInfo(@RequestHeader("Authorization") String encoded_credentials){
        encoded_credentials = encoded_credentials.split(" ")[1];
        String decoded_credentials = new String(Base64.decodeBase64(encoded_credentials));

        try{
            return ResponseEntity.ok().body(usersService.getAdminInfo(decoded_credentials.split(":")[0]));
        } catch (AdminNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/patient")
    public ResponseEntity<?> registerPatient(@RequestBody User patient){
        try {
            usersService.registerPatient(patient);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (PatientAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/doctor")
    public ResponseEntity<?> registerDoctor(@RequestBody User doctor){
        try {
            usersService.registerDoctor(doctor);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (DoctorAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody User admin){
        try {
            usersService.registerAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (AdminAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.FOUND).body(e.getMessage());
        }
    }
}
