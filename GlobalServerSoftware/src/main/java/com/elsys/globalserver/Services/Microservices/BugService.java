package com.elsys.globalserver.Services.Microservices;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.DataAccess.BugsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BugService {
    private final BugsRepository bugsRepository;

    @Autowired
    public BugService(BugsRepository bugsRepository) {
        this.bugsRepository = bugsRepository;
    }

    public void reportBug(Bug bug) {
        bugsRepository.save(bug);
    }

    public List<Bug> getBugs() {
        Iterable<Bug> bugs = bugsRepository.findAll();
        return StreamSupport.stream(bugs.spliterator(), false)
                .collect(Collectors.toList());
    }

    public boolean clearBug(int bug_id) {
        Optional<Bug> bug = bugsRepository.findById(bug_id);

        if (bug.isEmpty())
            return false;

        bugsRepository.deleteById(bug_id);
        return true;
    }
}
