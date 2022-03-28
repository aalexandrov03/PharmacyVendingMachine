package com.elsys.machine.DataAccess;

import com.elsys.machine.Models.Medicine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedicinesRepositoryTest {
    @Autowired
    private MedicinesRepository medicinesRepository;

    private List<Medicine> testMedicines;

    @BeforeAll
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

        medicine1 = medicinesRepository.save(medicine1);
        medicine2 = medicinesRepository.save(medicine2);

        testMedicines = List.of(medicine1, medicine2);
    }

    @AfterAll
    void tearDown(){
        medicinesRepository.deleteAll();
    }

    @Test
    void findAllByNeedsPrescription() {
        List<Medicine> medicines1 = StreamSupport.stream(
                medicinesRepository.findAllByNeedsPrescription(false).spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(medicines1.contains(testMedicines.get(0)));

        List<Medicine> medicines2 = StreamSupport.stream(
                medicinesRepository.findAllByNeedsPrescription(true).spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(medicines2.contains(testMedicines.get(1)));
    }

    @Test
    void findMedicineByName() {
        Optional<Medicine> medicine1 = medicinesRepository.findMedicineByName("Analgin");
        Optional<Medicine> medicine2 = medicinesRepository.findMedicineByName("Benalgin");

        assertTrue(medicine1.isPresent());
        assertTrue(medicine2.isEmpty());
    }
}