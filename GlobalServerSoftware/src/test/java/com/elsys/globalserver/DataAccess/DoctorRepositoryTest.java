package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Doctor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


@DataJpaTest
class DoctorRepositoryTest {
    @Autowired
    private DoctorRepository doctorRepository;

    @Test
    void findByEmail() {
        String email = "example@gmail.com";

        Doctor expectedDoctor = new Doctor();
        expectedDoctor.setEmail(email);
        expectedDoctor.setFullName("Full Name");
        expectedDoctor.setPassword("password");
        expectedDoctor.setRegion("Region");
        expectedDoctor.setUin(11111111L);

        doctorRepository.save(expectedDoctor);

        Optional<Doctor> actualDoctor = doctorRepository.findByEmail(email);

        if (actualDoctor.isEmpty())
            fail("Doctor not found!");

        assertEquals(expectedDoctor, actualDoctor.get());
    }
}