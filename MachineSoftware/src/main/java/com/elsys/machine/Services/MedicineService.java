package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Exceptions.MedicineAlreadyExistsException;
import com.elsys.machine.Exceptions.MedicineNotFoundException;
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
                throw new IllegalArgumentException(option + " is not a valid argument!");
        }
    }

    public Medicine getMedicineByName(String name) throws MedicineNotFoundException{
        Optional<Medicine> medicine = medicinesRepository.findMedicineByName(name);

        if (medicine.isEmpty())
            throw new MedicineNotFoundException(name);

        return medicine.get();
    }

    public void addMedicine(Medicine medicine) throws MedicineAlreadyExistsException {
        if (medicinesRepository.findMedicineByName(medicine.getName()).isPresent())
            throw new MedicineAlreadyExistsException(medicine.getName());

        medicinesRepository.save(medicine);
    }

    public void deleteMedicine(String medicineName) throws MedicineNotFoundException {
        Optional<Medicine> medicine = medicinesRepository.findMedicineByName(medicineName);

        if (medicine.isEmpty())
            throw new MedicineNotFoundException(medicineName);

        medicinesRepository.deleteById(medicine.get().getId());
    }

    public void updateMedicine(String medicineName, Medicine newMedicine) throws MedicineNotFoundException {
        Optional<Medicine> medicine = medicinesRepository.findMedicineByName(medicineName);

        if (medicine.isEmpty())
            throw new MedicineNotFoundException(medicineName);

        medicine.get().setMedicine(newMedicine);
        medicinesRepository.save(medicine.get());
    }
}
