package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.Services.CasualUsersService;
import com.elsys.globalserver.DB_Entities.CasualUser;
import com.elsys.globalserver.DB_Entities.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
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
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        Optional<CasualUser> user = casualUsersService.login(request.get("username"),
                                                             request.get("password"));

        if (user.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(user.get().getId());
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<?> getPrescriptions(@RequestParam int user_id) {
        Set<Prescription> prescriptions = casualUsersService.getAllUserPrescriptions(user_id);
        return ResponseEntity.ok().body(prescriptions);
    }

    @PostMapping("/bug")
    public ResponseEntity<?> submitBug(@RequestBody Bug bug) {
        casualUsersService.reportBug(bug);
        return ResponseEntity.ok().build();
    }
}
