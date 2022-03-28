package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Prescription;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrescriptionRepositoryTest {
    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    private String doctor;
    private String patient;
    private Prescription testPrescription;

    @BeforeAll
    void setUp() {
        doctor = "doctor@gmail.com";
        patient = "patient@gmail.com";

        Medicine medicine = new Medicine();
        medicine.setName("Analgin");
        medicine.setNeedsPrescription(false);

        medicineRepository.save(medicine);

        testPrescription = new Prescription(doctor, patient);
        testPrescription.setMedicines(List.of(medicine, medicine, medicine));

        prescriptionRepository.save(testPrescription);
    }

    @Test
    void findByDoctor() {
        List<Prescription> prescriptions = prescriptionRepository.findByDoctor(doctor);

        if (prescriptions.size() != 1)
            fail();

        for (Prescription prescription : prescriptions) {
            if (!prescription.equals(testPrescription))
                fail();
        }
    }

    @Test
    void findByPatient() {
        List<Prescription> prescriptions = prescriptionRepository.findByPatient(patient);

        if (prescriptions.size() != 1)
            fail();

        for (Prescription prescription : prescriptions) {
            if (!prescription.equals(testPrescription))
                fail();
        }
    }
}