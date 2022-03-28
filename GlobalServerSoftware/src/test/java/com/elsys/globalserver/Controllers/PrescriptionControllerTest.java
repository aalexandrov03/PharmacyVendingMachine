package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.DataAccess.PrescriptionRepository;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Patient;
import com.elsys.globalserver.Models.Prescription;
import com.elsys.globalserver.Services.Utils.PrescriptionDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrescriptionControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private MedicineRepository medicineRepository;
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    private Patient testPatient;
    private Doctor testDoctor;
    private List<Medicine> testMedicines;
    private Prescription testPrescription;

    @BeforeAll
    void setUpBeforeAll(){
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

        Medicine medicine1 = new Medicine();
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);

        Medicine medicine2 = new Medicine();
        medicine2.setName("Benalgin");
        medicine2.setNeedsPrescription(false);

        testMedicines = List.of(medicine1, medicine2);
        medicineRepository.saveAll(testMedicines);
    }

    @AfterAll
    void tearDownAfterAll(){
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @BeforeEach
    void setUpBeforeEach(){
        Prescription prescription = new Prescription(
                testDoctor.getEmail(),
                testPatient.getEmail()
        );
        prescription.setMedicines(testMedicines);

        testPrescription = prescriptionRepository.save(prescription);
    }

    @AfterEach
    void tearDownAfterEach(){
        prescriptionRepository.deleteAll();
    }

    @Test
    void getAllPrescriptions() {
        ResponseEntity<PrescriptionDTO[]> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/prescriptions/all", HttpMethod.GET,
                        new HttpEntity<>(""), PrescriptionDTO[].class);

        assertEquals(200, response.getStatusCodeValue());

        assertTrue(List.of(Objects.requireNonNull(response.getBody()))
                .contains(new PrescriptionDTO(testPrescription)));
    }

    @Test
    void getPatientPrescriptions() {
        ResponseEntity<PrescriptionDTO[]> response = restTemplate
                .withBasicAuth("p1@gmail.com", "patient1")
                .exchange("http://localhost:" + port + "/prescriptions/patient", HttpMethod.GET,
                        new HttpEntity<>(""), PrescriptionDTO[].class);

        assertEquals(200, response.getStatusCodeValue());

        assertTrue(List.of(Objects.requireNonNull(response.getBody()))
                .contains(new PrescriptionDTO(testPrescription)));
    }

    @Test
    void getDoctorPrescriptions() {
        ResponseEntity<PrescriptionDTO[]> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions/doctor", HttpMethod.GET,
                        new HttpEntity<>(""), PrescriptionDTO[].class);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(List.of(Objects.requireNonNull(response.getBody()))
                .contains(new PrescriptionDTO(testPrescription)));
    }

    @Test
    void getPrescriptionByID() {
        ResponseEntity<PrescriptionDTO> response = restTemplate
                .withBasicAuth("machine", "machine")
                .exchange("http://localhost:" + port + "/prescriptions/machine/" + testPrescription.getId(),
                        HttpMethod.GET, new HttpEntity<>(""), PrescriptionDTO.class);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(new PrescriptionDTO(testPrescription), response.getBody());
    }

    @Test
    void addPrescription() {
        Prescription prescription = new Prescription(
                testDoctor.getEmail(),
                testPatient.getEmail()
        );
        prescription.setMedicines(List.of(testMedicines.get(0)));

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions?patient_email="
                        + testPatient.getEmail(), HttpMethod.POST,
                        new HttpEntity<>(List.of(new MedOrder("Analgin", 1))), String.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Prescription> actualPresciptions = StreamSupport.stream(
                prescriptionRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(actualPresciptions.contains(prescription));
    }

    @Test
    void addPrescriptionPatientNotFound(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions?patient_email=1@gmail.com", HttpMethod.POST,
                        new HttpEntity<>(List.of(new MedOrder("Analgin", 1))), String.class);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Patient does not exist!", Objects.requireNonNull(response.getBody()));
    }

    @Test
    void addPrescriptionMedicineNotFound(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions?patient_email="
                                + testPatient.getEmail(), HttpMethod.POST,
                        new HttpEntity<>(List.of(new MedOrder("Espumizan", 1))), String.class);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Medicine does not exist!", Objects.requireNonNull(response.getBody()));
    }
    @Test
    void deletePrescription() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/prescriptions?id=" + testPrescription.getId(),
                        HttpMethod.DELETE, new HttpEntity<>(""), String.class);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void deletePrescriptionNotExist(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/prescriptions?id=2",
                        HttpMethod.DELETE, new HttpEntity<>(""), String.class);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void changeValidationPrescription() {
        boolean value = false;

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions?id="
                                + testPrescription.getId() + "&valid=" + value,
                        HttpMethod.PUT, new HttpEntity<>(""), String.class);

        assertEquals(200, response.getStatusCodeValue());

        Optional<Prescription> actualPrescription = prescriptionRepository.findById(
                testPrescription.getId()
        );

        if (actualPrescription.isEmpty())
            fail();

        assertEquals(value, actualPrescription.get().isValid());
    }

    @Test
    void changeValidationPrescriptionNotExist() {
        boolean value = false;

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("d1@gmail.com", "doctor1")
                .exchange("http://localhost:" + port + "/prescriptions?id=4&valid=" + value,
                        HttpMethod.PUT, new HttpEntity<>(""), String.class);

        assertEquals(404, response.getStatusCodeValue());
    }
}