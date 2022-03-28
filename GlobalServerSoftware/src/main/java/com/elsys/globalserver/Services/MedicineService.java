package com.elsys.globalserver.Services;

import com.elsys.globalserver.Models.Medicine;
import com.elsys.globalserver.DataAccess.MedicineRepository;
import com.elsys.globalserver.Exceptions.Medicines.MedicineAlreadyExistsException;
import com.elsys.globalserver.Exceptions.Medicines.MedicineNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MedicineService {
    private final MedicineRepository medicinesRepository;

    @Autowired
    public MedicineService(MedicineRepository medicinesRepository) {
        this.medicinesRepository = medicinesRepository;
    }

    public List<Medicine> getMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAll();
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void addMedicine(Medicine medicine) throws MedicineAlreadyExistsException {
        Optional<Medicine> medicine1 = medicinesRepository.findByName(medicine.getName());

        if (medicine1.isPresent())
            throw new MedicineAlreadyExistsException();

        medicinesRepository.save(medicine);
    }

    @Transactional
    public void deleteMedicine(String name) throws MedicineNotFoundException{
        Optional<Medicine> medicine1 = medicinesRepository.findByName(name);

        if (medicine1.isEmpty())
            throw new MedicineNotFoundException();

        medicinesRepository.deleteByName(name);
    }

    public void updateMedicine(String name, Medicine medicine) throws MedicineNotFoundException {
        Optional<Medicine> medicine1 = medicinesRepository.findByName(name);

        if (medicine1.isEmpty())
            throw new MedicineNotFoundException();

        medicine1.get().setMedicine(medicine);
        medicinesRepository.save(medicine1.get());
    }
}
