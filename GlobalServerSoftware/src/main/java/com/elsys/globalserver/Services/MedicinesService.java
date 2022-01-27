package com.elsys.globalserver.Services;

import com.elsys.globalserver.DB_Entities.Medicine;
import com.elsys.globalserver.DataAccess.MedicinesRepository;
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
    private final MedicinesRepository medicinesRepository;

    @Autowired
    public MedicinesService(MedicinesRepository medicinesRepository) {
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
                    throw new MedicineAlreadyExistsException(m);
            }
        }

        medicinesRepository.saveAll(medicines);
    }

    public void deleteMedicines(List<Integer> medicine_ids) throws MedicineNotFoundException{
        for (int id: medicine_ids){
            Optional<Medicine> medicine = medicinesRepository.findById(id);
            
            if (medicine.isEmpty())
                throw new MedicineNotFoundException(id);
        }
        medicinesRepository.deleteAllById(medicine_ids);
    }
}
