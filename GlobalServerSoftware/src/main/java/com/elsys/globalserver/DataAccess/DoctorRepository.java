package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Doctor;
import org.springframework.data.repository.CrudRepository;

public interface DoctorRepository extends CrudRepository<Doctor, Integer> {
}
