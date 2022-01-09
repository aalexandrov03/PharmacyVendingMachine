package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Prescription;
import com.elsys.globalserver.Services.DoctorsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/doctor/api")
public class DoctorAPIController {
    private final DoctorsService doctorsService;

    @Autowired
    public DoctorAPIController(DoctorsService doctorsService) {
        this.doctorsService = doctorsService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerDoctor(@RequestBody Doctor doctor) {
        boolean registered = doctorsService.register(doctor);

        if (!registered)
            return ResponseEntity.status(500).build();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        Optional<Doctor> doctor = doctorsService.login(request.get("username"),
                                                       request.get("password"));

        if (doctor.isEmpty())
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(doctor.get().getId());
    }

    @PostMapping("/prescription")
    public ResponseEntity<?> addPrescription(@RequestParam int doctor_id,
                                             @RequestParam String username,
                                             @RequestBody List<Integer> med_ids) {
        boolean status = doctorsService.addPrescription(username, doctor_id, med_ids);

        if (!status)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<?> getPrescriptions(@RequestParam int doctor_id) {
        Set<Prescription> prescriptions = doctorsService.getDoctorPrescriptions(doctor_id);
        return ResponseEntity.ok().body(prescriptions);
    }

    @GetMapping("/medicines")
    public ResponseEntity<?> getMedicines() {
        return ResponseEntity.ok().body(doctorsService.getAllMedicines());
    }

    @PostMapping("/bug")
    public ResponseEntity<?> submitBug(@RequestBody Bug bug) {
        doctorsService.reportBug(bug);
        return ResponseEntity.ok().build();
    }
}
