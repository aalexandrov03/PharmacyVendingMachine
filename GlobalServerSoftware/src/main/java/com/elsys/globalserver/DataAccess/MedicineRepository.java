package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Medicine;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface MedicineRepository extends CrudRepository<Medicine, Integer> {
    Optional<Medicine> findByName(String name);
}
