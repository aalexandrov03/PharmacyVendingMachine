package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Models.Medicine;
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

    public List<Medicine> getMedicines(String option) throws IllegalArgumentException {
        switch (option) {
            case "both":
                return StreamSupport.stream(
                                medicinesRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

            case "yes":
                return StreamSupport.stream(
                                medicinesRepository.findAllByNeedsPrescription(true).spliterator(),
                                false)
                        .collect(Collectors.toList());

            case "no":
                return StreamSupport.stream(
                                medicinesRepository.findAllByNeedsPrescription(false).spliterator(),
                                false)
                        .collect(Collectors.toList());

            default:
                throw new IllegalArgumentException();
        }
    }

    public void addMedicine(Medicine medicine) throws Exception {
        if (medicinesRepository.findMedicineByName(medicine.getName()).isPresent())
            throw new Exception(medicine.getName() + " already exists!");

        medicinesRepository.save(medicine);
    }

    public void deleteMedicine(String medicineName) throws Exception {
        Optional<Medicine> medicine = medicinesRepository.findMedicineByName(medicineName);

        if (medicine.isEmpty())
            throw new Exception(medicineName + " not found!");

        medicinesRepository.deleteById(medicine.get().getId());
    }

    public void updateMedicine(String medicineName, Medicine newMedicine) throws Exception {
        Optional<Medicine> medicine = medicinesRepository.findMedicineByName(medicineName);

        if (medicine.isEmpty())
            throw new Exception(medicineName + " not found!");

        medicine.get().setMedicine(newMedicine);
        medicinesRepository.save(medicine.get());
    }
}
