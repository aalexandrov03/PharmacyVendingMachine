package com.elsys.machine.DataAccess;


import com.elsys.machine.DB_Entities.Tests;
import org.springframework.data.repository.CrudRepository;

public interface TestsRepository extends CrudRepository<Tests, Integer> {
}
