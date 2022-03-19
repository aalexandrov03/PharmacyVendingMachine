package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
class PatientRepositoryTest {
    @Autowired
    private PatientRepository patientRepository;

    @Test
    void findByEmail() {
        String email = "example@gmail.com";

        Patient expectedPatient = new Patient();
        expectedPatient.setEmail(email);
        expectedPatient.setFullName("Full Name");
        expectedPatient.setPassword("password");

        patientRepository.save(expectedPatient);

        Optional<Patient> actualPatient = patientRepository.findByEmail(email);

        if (actualPatient.isEmpty())
            fail("Doctor not found!");

        assertEquals(expectedPatient, actualPatient.get());
    }
}