package com.elsys.globalserver.Services;

import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.Exceptions.Medicines.MedicineAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MedicinesService {
    private final MedicineRepository medicinesRepository;

    @Autowired
    public MedicinesService(MedicineRepository medicinesRepository) {
        this.medicinesRepository = medicinesRepository;
    }

    public List<Medicine> getMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void addMedicines(List<Medicine> medicines) throws MedicineAlreadyExistsException {
        for (Medicine m: medicines){
            for (Medicine m_repo: medicinesRepository.findAll()){
                if (m.equals(m_repo))
                    throw new MedicineAlreadyExistsException();
            }
        }

        medicinesRepository.saveAll(medicines);
    }

    public void deleteMedicines(List<String> medicines) throws MedicineNotFoundException{
        for (String name: medicines){
            Optional<Medicine> medicine = medicinesRepository.findByName(name);
            
            if (medicine.isEmpty())
                throw new MedicineNotFoundException();

            medicinesRepository.deleteById(medicine.get().getId());
        }
    }
}
