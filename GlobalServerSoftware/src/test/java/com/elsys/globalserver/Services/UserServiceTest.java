package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.Exceptions.Users.DoctorNotFoundException;
import com.elsys.globalserver.Exceptions.Users.PatientAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Users.PatientNotFoundException;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Patient;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    private UserService userService;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    private Patient patient;
    private Doctor doctor;

    @BeforeAll
    void setUp(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        patient = new Patient();
        patient.setFullName("Vasko Paligorov");
        patient.setEmail("vpal@gmail.com");
        patient.setPassword("pass");
        String patient_email = patient.getEmail();

        doctor = new Doctor();
        doctor.setFullName("Зорница Атанасова Маслева");
        doctor.setEmail("zor@gmail.com");
        doctor.setPassword("password");
        doctor.setUin(2300003990L);
        doctor.setRegion("София-град");
        String doctor_email = doctor.getEmail();

        patientRepository = Mockito.mock(PatientRepository.class);
        Mockito.when(patientRepository.findByEmail(patient_email))
                .thenReturn(Optional.empty());

        doctorRepository = Mockito.mock(DoctorRepository.class);
        Mockito.when(doctorRepository.findByEmail(doctor_email))
                .thenReturn(Optional.empty());

        userService = new UserService(
                patientRepository,
                doctorRepository,
                passwordEncoder
        );
    }

    @Test
    @Order(1)
    void registerPatient() throws PatientAlreadyExistsException {
        ArgumentCaptor<Patient> patientArgumentCaptor
                = ArgumentCaptor.forClass(Patient.class);

        userService.registerPatient(patient);

        Mockito.verify(patientRepository).save(patientArgumentCaptor.capture());

        assertEquals(patient, patientArgumentCaptor.getValue());
    }

    @Test
    @Order(2)
    void registerDoctor() throws Exception {
        ArgumentCaptor<Doctor> doctorArgumentCaptor
                = ArgumentCaptor.forClass(Doctor.class);

        userService.registerDoctor(doctor);

        Mockito.verify(doctorRepository).save(doctorArgumentCaptor.capture());

        assertEquals(doctor, doctorArgumentCaptor.getValue());
    }

    @Test
    @Order(3)
    void getPatientInfo() throws PatientNotFoundException {
        Mockito.when(patientRepository.findByEmail("vpal@gmail.com"))
                .thenReturn(Optional.of(patient));

        Patient patient1 = userService.getPatientInfo("vpal@gmail.com");
        assertEquals(patient, patient1);
    }

    @Test
    @Order(4)
    void getDoctorInfo() throws DoctorNotFoundException {
        Mockito.when(doctorRepository.findByEmail("zor@gmail.com"))
                .thenReturn(Optional.of(doctor));

        Doctor doctor1 = userService.getDoctorInfo("zor@gmail.com");
        assertEquals(doctor, doctor1);
    }
}