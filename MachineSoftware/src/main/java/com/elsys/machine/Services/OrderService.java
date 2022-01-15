package com.elsys.machine.Services;

import com.elsys.machine.DB_Entities.Medicine;
import com.elsys.machine.DB_Entities.Status;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.DataAccess.StatusRepository;
import com.elsys.machine.Services.Microservices.ExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {
    private final MedicinesRepository medicinesRepository;
    private final StatusRepository statusRepository;
    private final ExecutionService executionService;

    @Autowired
    public OrderService(MedicinesRepository medicinesRepository,
                        StatusRepository statusRepository,
                        ExecutionService executionService) {
        this.medicinesRepository = medicinesRepository;
        this.statusRepository = statusRepository;
        this.executionService = executionService;
    }

    public List<Medicine> getNoPrescriptionMedicines() {
        Iterable<Medicine> medicines = medicinesRepository.findAllByNeedsPrescription(false);
        return StreamSupport.stream(medicines.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Boolean getStatus() {
        Optional<Status> lastStatus = statusRepository.findTopByOrderByIdDesc();

        if (lastStatus.isEmpty())
            return null;

        return lastStatus.get().isActive();
    }

    public int validatePrescription(List<Medicine> prescription) {
        return executionService.validatePrescription(prescription);
    }

    public boolean executePrescription(List<Medicine> prescription) {
        return executionService.executePrescription(prescription);
    }
}