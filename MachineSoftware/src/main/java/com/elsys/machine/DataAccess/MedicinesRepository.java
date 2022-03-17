package com.elsys.machine.DataAccess;

import com.elsys.machine.Models.Medicine;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MedicinesRepository extends CrudRepository<Medicine, Integer> {
    Iterable<Medicine> findAllByNeedsPrescription(boolean needsPrescription);

    Optional<Medicine> findMedicineByName(String name);
}
