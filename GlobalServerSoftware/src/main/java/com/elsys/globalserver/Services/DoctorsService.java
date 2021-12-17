package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.*;
import com.elsys.globalserver.DataAccess.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class DoctorsService {
    private final MedicinesRepository medicinesRepository;
    private final DoctorRepository doctorRepository;
    private final CasualUserRepository casualUserRepository;
    private final PrescriptionsRepository prescriptionsRepository;
    private final BugsRepository bugsRepository;

    @Autowired
    public DoctorsService(MedicinesRepository medicinesRepository,
                          DoctorRepository doctorRepository,
                          CasualUserRepository casualUserRepository,
                          PrescriptionsRepository prescriptionsRepository,
                          BugsRepository bugsRepository) {
        this.medicinesRepository = medicinesRepository;
        this.doctorRepository = doctorRepository;
        this.casualUserRepository = casualUserRepository;
        this.prescriptionsRepository = prescriptionsRepository;
        this.bugsRepository = bugsRepository;
    }

    public List<Medicine> getAllMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean register(Doctor doctor_data, String uin) {
        Iterable<Doctor> doctors = doctorRepository.findAll();

        boolean username_available = true;
        for (Doctor doctor : doctors) {
            if (doctor_data.getUsername().equals(doctor.getUsername())) {
                username_available = false;
                break;
            }
        }

        if (username_available) {
            if(checkDoctor(doctor_data, uin)){
                doctorRepository.save(doctor_data);
                return true;
            }
        }

        return false;
    }

    public Doctor login(String username, String password) {
        Iterable<Doctor> doctors = doctorRepository.findAll();

        for (Doctor doctor : doctors) {
            if (doctor.getUsername().equals(username)
                    && doctor.getPassword().equals(password)) {
                return doctor;
            }
        }

        return null;
    }

    public boolean addPrescription(String username,
                                   int doctor_id,
                                   List<Integer> med_ids) {
        Optional<CasualUser> user = casualUserRepository.findByUsername(username);
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);
        List<Medicine> medicines = new ArrayList<>();

        for (int med_id : med_ids) {
            Optional<Medicine> medicine = medicinesRepository.findById(med_id);

            if (medicine.isEmpty())
                return false;

            medicines.add(medicine.get());
        }

        Prescription prescription = new Prescription();

        if (user.isPresent() && doctor.isPresent()) {
            prescription.setUser(user.get());
            prescription.setDoctor(doctor.get());

            for (Medicine medicine : medicines)
                prescription.addMedicine(medicine);

            prescriptionsRepository.save(prescription);
            return true;
        }

        return false;
    }

    public Set<Prescription> getDoctorPrescriptions(int doctor_id) {
        Optional<Doctor> doctor = doctorRepository.findById(doctor_id);
        return doctor.map(Doctor::getPrescriptions).orElse(null);
    }

    public void deletePrescription(int prescription_id) {
        prescriptionsRepository.deleteById(prescription_id);
    }

    public void reportBug(Bug bug) {
        bugsRepository.save(bug);
    }

    private boolean checkDoctor(Doctor doctor, String uin){
        WebClient client = new WebClient(BrowserVersion.CHROME);
        client.getOptions().setCssEnabled(false);
        client.getOptions().setThrowExceptionOnFailingStatusCode(false);
        client.getOptions().setThrowExceptionOnScriptError(false);
        client.getOptions().setPrintContentOnFailingStatusCode(false);

        HtmlPage page;
        try {
             page = client.getPage("https://blsbg.eu/bg/medics/search?DocSearch[uin]="+uin);
        } catch (IOException e) {
            return false;
        }
        HtmlTable node = (HtmlTable) page.getByXPath("//table[@class='items']").get(0);

        if (node.getRowCount() == 1)
            return false;

        String name = node.getRow(1).getCell(2).asNormalizedText();
        return name.equals(doctor.getFullName());
    }
}
