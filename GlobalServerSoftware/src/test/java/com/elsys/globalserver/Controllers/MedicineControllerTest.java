package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.Models.Medicine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedicineControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private MedicineRepository medicineRepository;

    private List<Medicine> testMedicines;

    @BeforeEach
    void setUpBeforeEach(){
        Medicine medicine1 = new Medicine();
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);

        Medicine medicine2 = new Medicine();
        medicine2.setName("Benalgin");
        medicine2.setNeedsPrescription(false);

        testMedicines = List.of(medicine1, medicine2);
        medicineRepository.saveAll(testMedicines);
    }

    @AfterEach
    void tearDownAfterEach(){
        medicineRepository.deleteAll();
    }

    @Test
    void getMedicines() {
        ResponseEntity<Medicine[]> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port + "/medicines", Medicine[].class);

        if (response.getStatusCodeValue() != 200)
            fail();

        if (response.getBody() == null)
            fail();

        assertTrue(testMedicines.containsAll(List.of(response.getBody())));
    }

    @Test
    void addMedicine() {
        Medicine medicine = new Medicine();
        medicine.setName("Paracetamol");
        medicine.setNeedsPrescription(true);

        ResponseEntity<Medicine> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("http://localhost:" + port + "/medicines", medicine, Medicine.class);

        List<Medicine> actualMedicines = StreamSupport.stream(
                medicineRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(actualMedicines.contains(medicine));
    }

    @Test
    void addMedicineAlreadyExist(){
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .postForEntity("http://localhost:" + port + "/medicines",
                        testMedicines.get(0), String.class);

        if (response.getStatusCodeValue() != 302)
            fail();
    }

    @Test
    void deleteMedicine() {
        restTemplate
                .withBasicAuth("admin", "admin")
                .delete("http://localhost:" + port + "/medicines?name="
                        + testMedicines.get(0).getName());

        List<Medicine> actualMedicines = StreamSupport
                .stream(medicineRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        assertFalse(actualMedicines.contains(testMedicines.get(0)));
    }

    @Test
    void deleteMedicineNotExist() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/medicines?name=Paracetamol",
                HttpMethod.DELETE, new HttpEntity<>(""), String.class);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void updateMedicine() {
        Medicine medicine = new Medicine();
        medicine.setName("Paracetamol");
        medicine.setNeedsPrescription(true);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port
                        + "/medicines?name=" + testMedicines.get(0).getName(),
                        HttpMethod.PUT, new HttpEntity<>(medicine), String.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Medicine> actualMedicines = StreamSupport.stream(
                medicineRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(actualMedicines.contains(medicine));
    }

    @Test
    void updateMedicineNotExist(){
        Medicine medicine = new Medicine();
        medicine.setName("Paracetamol");
        medicine.setNeedsPrescription(true);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port
                                + "/medicines?name=Espumizan",
                        HttpMethod.PUT, new HttpEntity<>(medicine), String.class);

        assertEquals(404, response.getStatusCodeValue());
    }
}