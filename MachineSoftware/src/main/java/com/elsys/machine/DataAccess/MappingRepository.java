package com.elsys.machine.DataAccess;

import com.elsys.machine.Models.Mapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MappingRepository extends CrudRepository<Mapping, Long> {
    Optional<Mapping> findBySlotID(long slotID);
}