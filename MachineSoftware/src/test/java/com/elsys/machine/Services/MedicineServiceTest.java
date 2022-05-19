package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Models.Medicine;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MedicineServiceTest {
    private MedicinesRepository medicinesRepository;
    private MedicineService medicineService;

    private List<Medicine> testMedicines;
    private ArgumentCaptor<Medicine> medicineArgumentCaptor;

    @BeforeAll
    void setUp(){
        medicinesRepository = Mockito.mock(MedicinesRepository.class);
        medicineService = new MedicineService(medicinesRepository);
        medicineArgumentCaptor = ArgumentCaptor.forClass(Medicine.class);

        Medicine medicine1 = new Medicine();
        medicine1.setId(1);
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);
        medicine1.setPrice(2.50);
        medicine1.setAmount(5);

        Medicine medicine2 = new Medicine();
        medicine2.setId(2);
        medicine2.setName("Paracetamol");
        medicine2.setNeedsPrescription(true);
        medicine2.setPrice(5.50);
        medicine2.setAmount(5);

        testMedicines = List.of(medicine1, medicine2);

        Mockito.when(medicinesRepository.findAll()).thenReturn(testMedicines);
        Mockito.when(medicinesRepository.findAllByNeedsPrescription(false))
                .thenReturn(List.of(testMedicines.get(0)));
        Mockito.when(medicinesRepository.findAllByNeedsPrescription(true))
                .thenReturn(List.of(testMedicines.get(1)));
        Mockito.when(medicinesRepository.save(any(Medicine.class))).thenReturn(null);
        Mockito.when(medicinesRepository.findMedicineByName("Analgin"))
                .thenReturn(Optional.of(testMedicines.get(0)));
        Mockito.when(medicinesRepository.findMedicineByName("Paracetamol"))
                .thenReturn(Optional.of(testMedicines.get(1)));
        Mockito.when(medicinesRepository.findMedicineByName("Benalgin"))
                .thenReturn(Optional.empty());
    }

    @Test
    @Order(1)
    void getMedicines() {
        List<Medicine> allMedicines = medicineService.getMedicines("both");
        assertTrue(allMedicines.containsAll(testMedicines));

        List<Medicine> medicinesNoPrescription = medicineService.getMedicines("no");
        assertTrue(medicinesNoPrescription.contains(testMedicines.get(0)));

        List<Medicine> medicinesWithPrescription = medicineService.getMedicines("yes");
        assertTrue(medicinesWithPrescription.contains(testMedicines.get(1)));
    }

    @Test
    @Order(2)
    void addMedicine() {
        Medicine medicine = new Medicine();
        medicine.setName("Benalgin");
        medicine.setNeedsPrescription(false);
        medicine.setPrice(3.50);
        medicine.setAmount(5);

        try{
            medicineService.addMedicine(medicine);
            Mockito.verify(medicinesRepository)
                    .save(medicineArgumentCaptor.capture());
            assertEquals(medicine, medicineArgumentCaptor.getValue());
        } catch (Exception e){
            fail();
        }

        assertThrows(Exception.class, () -> medicineService.addMedicine(testMedicines.get(0)));
    }

    @Test
    @Order(3)
    void deleteMedicine() {
        assertThrows(Exception.class, () -> medicineService.deleteMedicine("Benalgin"));

        ArgumentCaptor<Integer> integerArgumentCaptor
                = ArgumentCaptor.forClass(Integer.class);

        try {
            medicineService.deleteMedicine("Analgin");
            Mockito.verify(medicinesRepository)
                    .deleteById(integerArgumentCaptor.capture());

            Optional<Medicine> medicine = testMedicines.stream()
                    .filter(m -> m.getId() == integerArgumentCaptor.getValue())
            .findFirst();

            if (medicine.isEmpty())
                fail();

            assertEquals(testMedicines.get(0), medicine.get());
        } catch (Exception e){
            fail();
        }
    }

    @Test
    @Order(4)
    void updateMedicine() {
        Medicine medicine = new Medicine();
        medicine.setName("Benalgin");
        medicine.setNeedsPrescription(false);
        medicine.setPrice(3.50);
        medicine.setAmount(5);

        assertThrows(Exception.class, () -> medicineService.updateMedicine("Benalgin", medicine));

        try{
            medicineService.updateMedicine("Analgin", medicine);

            Mockito.verify(medicinesRepository, Mockito.times(2))
                    .save(medicineArgumentCaptor.capture());

            assertEquals(medicine, medicineArgumentCaptor.getValue());
        } catch (Exception e){
            fail();
        }
    }
}