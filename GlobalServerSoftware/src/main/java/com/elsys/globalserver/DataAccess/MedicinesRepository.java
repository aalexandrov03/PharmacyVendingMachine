package com.elsys.globalserver.DataAccess;

import com.elsys.globalserver.DB_Entities.Medicine;
import org.springframework.data.repository.CrudRepository;

public interface MedicinesRepository extends CrudRepository<Medicine, Integer> {
}
