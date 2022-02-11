package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DatabaseEntities.Medicine;
import org.springframework.data.repository.CrudRepository;

public interface MedicineRepository extends CrudRepository<Medicine, Integer> {

}
