package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Medicine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
class MedicineRepositoryTest {
    @Autowired
    private MedicineRepository medicineRepository;
    private Medicine expectedMedicine;

    @BeforeEach
    void setUp() {
        expectedMedicine = new Medicine();
        expectedMedicine.setName("Analgin");
        expectedMedicine.setNeedsPrescription(false);

        medicineRepository.save(expectedMedicine);
    }

    @AfterEach
    void tearDown() {
        medicineRepository.deleteAll();
    }

    @Test
    void findByName() {
        Optional<Medicine> actualMedicine = medicineRepository.findByName("Analgin");

        if (actualMedicine.isEmpty())
            fail("Medicine not found!");

        assertEquals(expectedMedicine, actualMedicine.get());
    }

    @Test
    void deleteByName() {
        medicineRepository.deleteByName("Analgin");

        Iterable<Medicine> medicines = medicineRepository.findAll();

        if (StreamSupport.stream(medicines.spliterator(), false).findAny().isPresent())
            fail("Medicine was not deleted!");
    }
}