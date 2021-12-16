package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Prescription;
import org.springframework.data.repository.CrudRepository;

public interface PrescriptionsRepository extends CrudRepository<Prescription, Integer> {
}
