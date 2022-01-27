package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Controllers.Utils.LoginData;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Patient;
import com.elsys.globalserver.Exceptions.Users.AdminAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.DoctorAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.PatientAlreadyExistsException;
import com.elsys.globalserver.Services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UsersController {
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("{role}")
    public ResponseEntity<?> login(@PathVariable String role,
                                   @RequestBody LoginData loginData) {
        switch (role) {
            case "patient":
                Optional<Patient> patient = usersService.loginPatient(
                        loginData.getUsername(),
                        loginData.getPassword()
                );

                if (patient.isEmpty())
                    return ResponseEntity.status(404).body("Wrong username or password!");

                return ResponseEntity.ok().body(patient.get());

            case "doctor":
                Optional<Doctor> doctor = usersService.loginDoctor(
                        loginData.getUsername(),
                        loginData.getPassword()
                );

                if (doctor.isEmpty())
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wrong username or password!");

                return ResponseEntity.ok().body(doctor.get());

            case "admin":
                boolean status = usersService.authenticateAdmin(
                        loginData.getUsername(),
                        loginData.getPassword()
                );

                if (!status)
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Wrong username or password!");

                return ResponseEntity.ok().build();

            default:
                return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("{role}")
    public ResponseEntity<?> register(@PathVariable String role,
                                      @RequestBody Map<String, String> registerData) {
        switch (role) {
            case "patient":
                if (checkRequest(registerData, role))
                    return ResponseEntity.badRequest().build();

                try{
                    usersService.registerPatient(new Patient(registerData.get("fullName"),
                            registerData.get("username"), registerData.get("password")));
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                } catch (PatientAlreadyExistsException exception){
                    return ResponseEntity.status(HttpStatus.FOUND).body(exception.getMessage());
                }

            case "doctor":
                if (checkRequest(registerData, role))
                    return ResponseEntity.badRequest().build();

                try{
                    boolean status = usersService.registerDoctor(new Doctor(registerData.get("fullName"),
                            registerData.get("uin"), registerData.get("username"),
                            registerData.get("password"), registerData.get("workplace")));

                    if (!status)
                        return ResponseEntity.internalServerError().body("Error verifying doctor info!");

                    return ResponseEntity.status(HttpStatus.CREATED).build();
                } catch (DoctorAlreadyExistsException exception){
                    return ResponseEntity.status(HttpStatus.FOUND).body(exception.getMessage());
                }

            case "admin":
                if (checkRequest(registerData, role))
                    return ResponseEntity.badRequest().build();

                try {
                    usersService.registerAdmin(registerData.get("username"),
                            registerData.get("password"));
                    return ResponseEntity.status(HttpStatus.CREATED).build();
                } catch (AdminAlreadyExistsException exception){
                    return ResponseEntity.status(HttpStatus.FOUND).body(exception.getMessage());
                }

            default:
                return ResponseEntity.badRequest().build();
        }
    }

    private boolean checkRequest(Map<String, String> request, String role) {
        switch (role) {
            case "patient":
                return request.keySet().size() != 3 || !request.containsKey("username") ||
                        !request.containsKey("password") || !request.containsKey("fullName");

            case "doctor":
                return request.keySet().size() != 5 || !request.containsKey("fullName") ||
                        !request.containsKey("workplace") || !request.containsKey("uin") ||
                        !request.containsKey("username") || !request.containsKey("password");

            case "admin":
                return request.keySet().size() != 2 || !request.containsKey("username") ||
                        !request.containsKey("password");

            default:
                return true;
        }
    }
}
