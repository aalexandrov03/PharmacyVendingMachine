package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.Models.Bug;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugRepository extends CrudRepository<Bug, Integer> {
}
