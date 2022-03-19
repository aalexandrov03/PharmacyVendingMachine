package com.elsys.globalserver.Security;

import com.elsys.globalserver.DataAccess.DoctorRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.Models.Doctor;
import com.elsys.globalserver.Models.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    @Autowired
    public CustomUserDetailsService(PatientRepository patientRepository,
                                    DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            return getPatientUserDetails(email);
        } catch (UsernameNotFoundException e1){
            try {
                return getDoctorUserDetails(email);
            } catch (UsernameNotFoundException e2){
                throw new UsernameNotFoundException("User not found");
            }
        }
    }

    private UserDetails getPatientUserDetails(String email) throws UsernameNotFoundException{
        Optional<Patient> patientOptional = patientRepository.findByEmail(email);

        if (patientOptional.isEmpty())
            throw new UsernameNotFoundException("Patient not found!");

        return User.builder()
                   .username(patientOptional.get().getEmail())
                   .password(patientOptional.get().getPassword())
                   .roles("PATIENT")
                   .accountExpired(false)
                   .accountLocked(false)
                   .credentialsExpired(false)
                   .disabled(false)
               .build();
    }

    private UserDetails getDoctorUserDetails(String email) throws UsernameNotFoundException{
        Optional<Doctor> doctorOptional = doctorRepository.findByEmail(email);

        if (doctorOptional.isEmpty())
            throw new UsernameNotFoundException("Doctor not found!");

        return User.builder()
                .username(doctorOptional.get().getEmail())
                .password(doctorOptional.get().getPassword())
                .roles("DOCTOR")
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
