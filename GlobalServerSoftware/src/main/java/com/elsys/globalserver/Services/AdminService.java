package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DataAccess.BugsRepository;
import com.elsys.globalserver.DataAccess.MedicinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class AdminService {
    private final MedicinesRepository medicinesRepository;
    private final BugsRepository bugsRepository;

    @Autowired
    public AdminService(MedicinesRepository medicinesRepository, BugsRepository bugsRepository) {
        this.medicinesRepository = medicinesRepository;
        this.bugsRepository = bugsRepository;
    }

    public boolean addMedicine(Medicine medicine) {
        Iterable<Medicine> medicines = medicinesRepository.findAll();

        for (Medicine m : medicines)
            if (medicine.equals(m))
                return false;

        medicinesRepository.save(medicine);
        return true;
    }

    public boolean deleteMedicine(int medicine_id) {
        Optional<Medicine> medicine = medicinesRepository.findById(medicine_id);

        if (medicine.isEmpty())
            return false;

        medicinesRepository.deleteById(medicine_id);
        return true;
    }

    public List<Medicine> getMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<Bug> getBugs() {
        Iterable<Bug> bugs = bugsRepository.findAll();
        return StreamSupport.stream(bugs.spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean clearBug(int bug_id) {
        Optional<Bug> bug = bugsRepository.findById(bug_id);

        if (bug.isEmpty())
            return false;

        bugsRepository.deleteById(bug_id);
        return true;
    }
}
