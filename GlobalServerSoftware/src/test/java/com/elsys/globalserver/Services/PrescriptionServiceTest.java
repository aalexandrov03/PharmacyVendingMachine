package com.elsys.globalserver.Services;

import com.elsys.globalserver.Controllers.Utils.MedOrder;
import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.DataAccess.PrescriptionRepository;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import com.elsys.globalserver.Exceptions.Prescriptions.PrescriptionNotFoundException;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.Models.Patient;
import com.elsys.globalserver.Models.Prescription;
import com.elsys.globalserver.Services.Utils.PrescriptionDTO;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PrescriptionServiceTest {
    private PrescriptionService prescriptionService;
    private PrescriptionRepository prescriptionRepository;

    private Prescription prescription;
    private String patient_email;
    private String doctor_email;
    private List<Medicine> testMedicines;

    @BeforeAll
    void setUp(){
        prescriptionRepository = Mockito.mock(PrescriptionRepository.class);
        PatientRepository patientRepository = Mockito.mock(PatientRepository.class);
        DoctorRepository doctorRepository = Mockito.mock(DoctorRepository.class);
        MedicineRepository medicineRepository = Mockito.mock(MedicineRepository.class);

        Patient patient = new Patient();
        patient.setFullName("Vasko Paligorov");
        patient.setEmail("vpal@gmail.com");
        patient.setPassword("pass");
        patient_email = patient.getEmail();

        Doctor doctor = new Doctor();
        doctor.setFullName("Tin4aka ne 4aka");
        doctor.setEmail("tin4ev@gmail.com");
        doctor.setPassword("tin4ak");
        doctor.setUin(111111L);
        doctor.setRegion("Sofia");
        doctor_email = doctor.getEmail();

        Medicine medicine = new Medicine();
        medicine.setName("Analgin");
        medicine.setNeedsPrescription(false);

        Medicine medicine1 = new Medicine();
        medicine1.setName("Paracetamol");
        medicine1.setNeedsPrescription(true);

        testMedicines = List.of(medicine, medicine1);

        prescription = new Prescription(doctor.getEmail(), patient.getEmail());
        prescription.setId(1);
        prescription.setMedicines(testMedicines);

        Mockito.when(prescriptionRepository.findAll()).thenReturn(List.of(prescription));
        Mockito.when(prescriptionRepository.findById(1)).thenReturn(Optional.of(prescription));
        Mockito.when(prescriptionRepository.findByDoctor(doctor_email))
                        .thenReturn(List.of(prescription));
        Mockito.when(prescriptionRepository.findByPatient(patient_email))
                .thenReturn(List.of(prescription));

        Mockito.doNothing().when(prescriptionRepository).deleteById(Mockito.anyInt());

        Mockito.when(doctorRepository.findByEmail(doctor_email)).thenReturn(Optional.of(doctor));

        Mockito.when(patientRepository.findByEmail(patient_email)).thenReturn(Optional.of(patient));

        Mockito.when(medicineRepository.findByName("Analgin")).thenReturn(Optional.of(medicine));
        Mockito.when(medicineRepository.findByName("Paracetamol")).thenReturn(Optional.of(medicine1));

        prescriptionService = new PrescriptionService(
                prescriptionRepository,
                patientRepository,
                doctorRepository,
                medicineRepository
        );
    }

    @Test
    void getAllPrescriptions() {
        List<PrescriptionDTO> prescriptionDTOS = prescriptionService.getAllPrescriptions();
        List<PrescriptionDTO> expectedPrescriptionDTOS = List.of(new PrescriptionDTO(prescription));

        assertTrue(prescriptionDTOS.containsAll(expectedPrescriptionDTOS));
    }

    @Test
    void getPrescriptionByID() throws PrescriptionNotFoundException {
        PrescriptionDTO prescriptionDTO = prescriptionService.getPrescriptionByID(1);
        assertEquals(new PrescriptionDTO(prescription), prescriptionDTO);
    }

    @Test
    void getDoctorPrescriptions() throws DoctorNotFoundException {
        List<PrescriptionDTO> prescriptionDTOS = prescriptionService
                .getDoctorPrescriptions(doctor_email);
        List<PrescriptionDTO> expectedPrescriptionDTOS = List.of(new PrescriptionDTO(prescription));

        assertTrue(prescriptionDTOS.containsAll(expectedPrescriptionDTOS));
    }

    @Test
    void getPatientPrescriptions() throws PatientNotFoundException {
        List<PrescriptionDTO> prescriptionDTOS = prescriptionService
                .getPatientPrescriptions(patient_email);
        List<PrescriptionDTO> expectedPrescriptionDTOS = List.of(new PrescriptionDTO(prescription));

        assertTrue(prescriptionDTOS.containsAll(expectedPrescriptionDTOS));
    }

    @Test
    void addPrescription() throws MedicineNotFoundException, PatientNotFoundException {
        ArgumentCaptor<Prescription> prescriptionArgumentCaptor
                = ArgumentCaptor.forClass(Prescription.class);

        prescriptionService.addPrescription(
                patient_email,
                doctor_email,
                List.of(
                        new MedOrder("Analgin", 1),
                        new MedOrder("Paracetamol", 1)
                )
        );

        Mockito.verify(prescriptionRepository, Mockito.times(2))
                .save(prescriptionArgumentCaptor.capture());

        assertEquals(patient_email, prescriptionArgumentCaptor.getValue().getPatient());
        assertEquals(doctor_email, prescriptionArgumentCaptor.getValue().getDoctor());
        assertTrue(prescriptionArgumentCaptor.getValue().getMedicines().containsAll(testMedicines));
    }

    @Test
    void changeValidationPrescriptions() throws PrescriptionNotFoundException {
        ArgumentCaptor<Prescription> prescriptionArgumentCaptor
                = ArgumentCaptor.forClass(Prescription.class);

        prescriptionService.changeValidationPrescriptions(
                doctor_email,
                1,
                false
        );

        Mockito.verify(prescriptionRepository)
                .save(prescriptionArgumentCaptor.capture());

        assertFalse(prescriptionArgumentCaptor.getValue().isValid());
    }

    @Test
    void deletePrescription() throws PrescriptionNotFoundException {
        ArgumentCaptor<Integer> integerArgumentCaptor
                = ArgumentCaptor.forClass(Integer.class);

        prescriptionService.deletePrescription(1);

        Mockito.verify(prescriptionRepository).deleteById(integerArgumentCaptor.capture());

        assertEquals(1, integerArgumentCaptor.getValue());
    }
}