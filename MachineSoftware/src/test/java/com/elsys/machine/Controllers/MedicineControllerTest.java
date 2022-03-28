package com.elsys.machine.Controllers;

import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Models.Medicine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MedicineControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private MedicinesRepository medicinesRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    private List<Medicine> testMedicines;

    @BeforeEach
    void setUp(){
        Medicine medicine1 = new Medicine();
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);
        medicine1.setPrice(2.50);
        medicine1.setAmount(5);

        Medicine medicine2 = new Medicine();
        medicine2.setName("Paracetamol");
        medicine2.setNeedsPrescription(true);
        medicine2.setPrice(5.50);
        medicine2.setAmount(5);

        testMedicines = (List<Medicine>) medicinesRepository.saveAll(List.of(medicine1, medicine2));
    }

    @AfterEach
    void tearDown(){
        medicinesRepository.deleteAll();
    }

    @Test
    void getMedicines() {
        Map<String, List<Medicine>> respKeys = new HashMap<>();
        respKeys.put("no", List.of(testMedicines.get(0)));
        respKeys.put("yes", List.of(testMedicines.get(1)));
        respKeys.put("both", testMedicines);

        for (String arg: respKeys.keySet()){
            ResponseEntity<Medicine[]> response = restTemplate
                    .exchange("http://localhost:" + port + "/medicines?prescription=" + arg, HttpMethod.GET,
                            new HttpEntity<>(""), Medicine[].class);

            assertEquals(200, response.getStatusCodeValue());
            assertTrue(response.hasBody());

            assertTrue(respKeys.get(arg).containsAll(
                    List.of(Objects.requireNonNull(response.getBody()))));
        }
    }

    @Test
    void addMedicine() {
        Medicine medicine1 = new Medicine();
        medicine1.setName("Benalgin");
        medicine1.setNeedsPrescription(false);
        medicine1.setPrice(3.50);
        medicine1.setAmount(5);

        ResponseEntity<String> response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/medicines", HttpMethod.POST,
                        new HttpEntity<>(medicine1), String.class);

        assertEquals(200, response.getStatusCodeValue());

        Medicine medicine2 = new Medicine();
        medicine2.setName("Benalgin");
        medicine2.setNeedsPrescription(false);
        medicine2.setPrice(3.50);
        medicine2.setAmount(5);

        response = restTemplate
                .withBasicAuth("admin", "admin")
                .exchange("http://localhost:" + port + "/medicines", HttpMethod.POST,
                        new HttpEntity<>(medicine2), String.class);

        assertEquals(302, response.getStatusCodeValue());
    }

    @Test
    void deleteMedicine() {
        Map<String, Integer> statusKeys = new HashMap<>();
        statusKeys.put("Analgin", 200);
        statusKeys.put("Benalgin", 404);

        for (String arg: statusKeys.keySet()){
            ResponseEntity<String> response = restTemplate
                    .withBasicAuth("admin", "admin")
                    .exchange("http://localhost:" + port + "/medicines?name=" + arg, HttpMethod.DELETE,
                            new HttpEntity<>(""), String.class);

            assertEquals(statusKeys.get(arg), response.getStatusCodeValue());

            if (response.getStatusCodeValue() == 200){
                List<Medicine> actualMedicines = StreamSupport.stream(
                        medicinesRepository.findAll().spliterator(), false
                ).collect(Collectors.toList());

                assertFalse(actualMedicines.contains(testMedicines.get(0)));
            }
        }
    }

    @Test
    void updateMedicine() {
        Medicine medicine = new Medicine();
        medicine.setName("Benalgin");
        medicine.setNeedsPrescription(false);
        medicine.setPrice(3.50);
        medicine.setAmount(5);

        Map<String, Integer> statusKeys = new LinkedHashMap<>();
        statusKeys.put("Benalgin", 404);
        statusKeys.put("Analgin", 200);


        for (String arg: statusKeys.keySet()){
            ResponseEntity<String> response = restTemplate
                    .withBasicAuth("admin", "admin")
                    .exchange("http://localhost:" + port + "/medicines?name=" + arg, HttpMethod.PUT,
                            new HttpEntity<>(medicine), String.class);

            assertEquals(statusKeys.get(arg), response.getStatusCodeValue());

            if (response.getStatusCodeValue() == 200){
                Optional<Medicine> actualMedicine = medicinesRepository.findMedicineByName("Benalgin");

                if (actualMedicine.isEmpty())
                    fail();

                assertEquals(medicine, actualMedicine.get());
            }
        }

    }
}