package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    private Patient testPatient;
    private Doctor testDoctor;

    @BeforeEach
    void setUp(){
        Patient patient = new Patient();
        patient.setFullName("Patient 1");
        patient.setEmail("p1@gmail.com");
        patient.setPassword(
                new BCryptPasswordEncoder(10).encode("patient1")
        );

        testPatient = patientRepository.save(patient);

        Doctor doctor = new Doctor();
        doctor.setFullName("Doctor 1");
        doctor.setEmail("d1@gmail.com");
        doctor.setPassword(
                new BCryptPasswordEncoder(10).encode("doctor1")
        );
        doctor.setUin(111111L);
        doctor.setRegion("Region 1");

        testDoctor = doctorRepository.save(doctor);
    }

    @AfterEach
    void tearDown(){
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    void registerPatient() {
        Patient patient = new Patient();
        patient.setFullName("Patient 2");
        patient.setEmail("p2@gmail.com");
        patient.setPassword("patient2");

        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:" + port + "/users/patient", HttpMethod.POST,
                        new HttpEntity<>(patient), String.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Patient> actualPatients = StreamSupport.stream(
                patientRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(actualPatients.contains(patient));
    }

    @Test
    void registerPatientAlreadyExists(){
        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:" + port + "/users/patient", HttpMethod.POST,
                        new HttpEntity<>(testPatient), String.class);

        assertEquals(302, response.getStatusCodeValue());
    }

    @Test
    void registerDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFullName("Зорница Атанасова Маслева");
        doctor.setEmail("zor@gmail.com");
        doctor.setPassword("password");
        doctor.setUin(2300003990L);
        doctor.setRegion("София-град");

        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:" + port + "/users/doctor", HttpMethod.POST,
                        new HttpEntity<>(doctor), String.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Doctor> actualDoctors = StreamSupport.stream(
                doctorRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(actualDoctors.contains(doctor));
    }

    @Test
    void registerDoctorAlreadyExists(){
        ResponseEntity<String> response = restTemplate
                .exchange("http://localhost:" + port + "/users/doctor", HttpMethod.POST,
                        new HttpEntity<>(testDoctor), String.class);

        assertEquals(302, response.getStatusCodeValue());
    }

    @Test
    void getPatientInfo() {
        ResponseEntity<Patient> response = restTemplate
                .withBasicAuth("p1@gmail.com", "patient1")
                .exchange("http://localhost:" + port + "/users/patient", HttpMethod.GET,
                        new HttpEntity<>(""), Patient.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(response.getBody(), testPatient);
    }

    @Test
    void getDoctorInfo() {
        ResponseEntity<Doctor> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/users/doctor", HttpMethod.GET,
                        new HttpEntity<>(""), Doctor.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(response.getBody(), testDoctor);
    }
}