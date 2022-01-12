package com.elsys.machine.Services.AdminService;

import com.elsys.machine.DB_Entities.Status;
import com.elsys.machine.DataAccess.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class StatusService {
    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getStatusHistory() {
        Iterable<Status> statusHistory = statusRepository.findAll();
        return StreamSupport.stream(statusHistory.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Status getLastStatus() {
        return statusRepository.findTopByOrderByIdDesc().orElse(null);
    }

    public void clearStatusHistory() {
        statusRepository.deleteAll();
    }

    public void setStatus(boolean status) {
        statusRepository.save(new Status(LocalDateTime.now().toString(), status));
    }
}
