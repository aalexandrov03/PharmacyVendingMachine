package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.CasualUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CasualUserRepository extends CrudRepository<CasualUser, Integer> {
    Optional<CasualUser> findByUsername(String username);
}
