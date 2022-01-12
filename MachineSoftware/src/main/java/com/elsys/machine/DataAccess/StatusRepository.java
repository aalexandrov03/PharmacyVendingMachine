package com.elsys.machine.DataAccess;

import com.elsys.machine.DB_Entities.Status;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StatusRepository extends CrudRepository<Status, Integer> {
    Optional<Status> findTopByOrderByIdDesc();
}
