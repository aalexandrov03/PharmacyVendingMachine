package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.Exceptions.Medicines.MedicineAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Models.Medicine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MedicineServiceTest {
    private MedicineRepository medicineRepository;
    private MedicineService medicineService;
    private List<Medicine> testMedicines;
    private ArgumentCaptor<Medicine> medicineArgumentCaptor;

    @BeforeAll
    void setUp() {
        medicineArgumentCaptor
                = ArgumentCaptor.forClass(Medicine.class);

        Medicine medicine1 = new Medicine();
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);

        Medicine medicine2 = new Medicine();
        medicine2.setName("Benalgin");
        medicine2.setNeedsPrescription(true);

        testMedicines = List.of(medicine1, medicine2);

        medicineRepository = Mockito.mock(MedicineRepository.class);
        Mockito.when(medicineRepository.findAll()).thenReturn(testMedicines);
        Mockito.when(medicineRepository.findByName("Analgin")).thenReturn(Optional.of(medicine1));
        Mockito.when(medicineRepository.save(Mockito.any(Medicine.class))).thenReturn(null);
        Mockito.doNothing().when(medicineRepository).deleteByName(Mockito.anyString());

        medicineService = new MedicineService(medicineRepository);
    }

    @Test
    void getMedicines() {
        List<Medicine> medicines = medicineService.getMedicines();
        assertTrue(medicines.containsAll(testMedicines));
    }

    @Test
    void addMedicine() throws MedicineAlreadyExistsException {
        Medicine medicine = new Medicine();
        medicine.setName("Benalgin");
        medicine.setNeedsPrescription(false);

        medicineService.addMedicine(medicine);

        Mockito.verify(medicineRepository).save(
                medicineArgumentCaptor.capture());

        assertEquals(medicine, medicineArgumentCaptor.getValue());
    }

    @Test
    void deleteMedicine() throws MedicineNotFoundException {
        medicineService.deleteMedicine("Analgin");

        ArgumentCaptor<String> stringArgumentCaptor
                = ArgumentCaptor.forClass(String.class);

        Mockito.verify(medicineRepository).deleteByName(
                stringArgumentCaptor.capture());

        assertEquals("Analgin", stringArgumentCaptor.getValue());
    }

    @Test
    void updateMedicine() throws MedicineNotFoundException {
        Medicine medicine = new Medicine();
        medicine.setName("Paracetamol");
        medicine.setNeedsPrescription(true);

        medicineService.updateMedicine("Analgin", medicine);

        Mockito.verify(medicineRepository, Mockito.times(2)).save(
                medicineArgumentCaptor.capture());

        assertEquals(medicine, medicineArgumentCaptor.getValue());
    }
}
