package com.elsys.globalserver.Services.Microservices;

import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DataAccess.MedicinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MedicineService {
    private final MedicinesRepository medicinesRepository;

    @Autowired
    public MedicineService(MedicinesRepository medicinesRepository) {
        this.medicinesRepository = medicinesRepository;
    }

    public List<Medicine> getMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
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
}
