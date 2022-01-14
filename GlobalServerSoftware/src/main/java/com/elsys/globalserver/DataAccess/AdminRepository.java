package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Integer> {
    Optional<Admin> findByUsernameAndPassword(String username, String password);
}
