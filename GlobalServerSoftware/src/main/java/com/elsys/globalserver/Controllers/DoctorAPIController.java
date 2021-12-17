package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.Services.DoctorsService;
import com.elsys.globalserver.DB_Entities.Doctor;
import com.elsys.globalserver.DB_Entities.Prescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<?> registerDoctor(@RequestBody Doctor doctor, @RequestHeader String uin) {
        boolean registered = doctorsService.register(doctor, uin);

        if (!registered)
            return ResponseEntity.status(500).build();

        return ResponseEntity.ok().build();
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(@RequestHeader String username,
                                   @RequestHeader String password) {
        Doctor doctor = doctorsService.login(username, password);

        if (doctor == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().body(doctor);
    }

    @PostMapping("/addPrescription")
    public ResponseEntity<?> addPrescription(@RequestHeader String username,
                                             @RequestHeader int doctor_id,
                                             @RequestBody List<Integer> med_ids) {
        boolean status = doctorsService.addPrescription(username, doctor_id, med_ids);

        if (!status)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/deletePrescription")
    public ResponseEntity<?> deletePrescription(@RequestHeader int prescription_id) {
        doctorsService.deletePrescription(prescription_id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getPrescriptions")
    public ResponseEntity<?> getPrescriptions(@RequestHeader int doctor_id) {
        Set<Prescription> prescriptions = doctorsService.getDoctorPrescriptions(doctor_id);
        return ResponseEntity.ok().body(prescriptions);
    }

    @GetMapping("/getMedicines")
    public ResponseEntity<?> getMedicines() {
        return ResponseEntity.ok().body(doctorsService.getAllMedicines());
    }

    @PostMapping("/submitBug")
    public ResponseEntity<?> submitBug(@RequestBody Bug bug) {
        doctorsService.reportBug(bug);
        return ResponseEntity.ok().build();
    }
}
