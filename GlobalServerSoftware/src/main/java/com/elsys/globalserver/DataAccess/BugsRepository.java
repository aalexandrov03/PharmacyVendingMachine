package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Bug;
import org.springframework.data.repository.CrudRepository;

public interface BugsRepository extends CrudRepository<Bug, Integer> {
}
