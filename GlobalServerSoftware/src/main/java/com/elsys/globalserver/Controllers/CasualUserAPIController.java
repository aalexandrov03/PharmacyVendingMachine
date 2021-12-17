package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.Services.CasualUsersService;
import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/casual/user/api")
public class CasualUserAPIController {
    private final CasualUsersService casualUsersService;

    @Autowired
    public CasualUserAPIController(CasualUsersService casualUsersService) {
        this.casualUsersService = casualUsersService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@RequestBody CasualUser casual_user) {
        boolean registered = casualUsersService.register(casual_user);

        if (!registered)
            return ResponseEntity.status(HttpStatus.FOUND).build();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader String username,
                                   @RequestHeader String password) {
        CasualUser user = casualUsersService.login(username, password);

        if (user == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(user);
    }

    @GetMapping("/getPrescriptions")
    public ResponseEntity<?> getPrescriptions(@RequestHeader int user_id) {
        Set<Prescription> prescriptions = casualUsersService.getAllUserPrescriptions(user_id);
        return ResponseEntity.ok().body(prescriptions);
    }

    @PostMapping("/submitBug")
    public ResponseEntity<?> submitBug(@RequestBody Bug bug) {
        casualUsersService.reportBug(bug);
        return ResponseEntity.ok().build();
    }
}
