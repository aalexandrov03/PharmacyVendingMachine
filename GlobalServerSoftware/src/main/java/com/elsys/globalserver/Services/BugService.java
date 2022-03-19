package com.elsys.globalserver.Services;

import com.elsys.globalserver.Models.Bug;
import com.elsys.globalserver.DataAccess.BugRepository;
import com.elsys.globalserver.Exceptions.Bugs.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BugService {
    private final BugRepository bugsRepository;

    @Autowired
    public BugService(BugRepository bugsRepository) {
        this.bugsRepository = bugsRepository;
    }

    public List<Bug> getBugs() {
        return StreamSupport.stream(bugsRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    public void clearBug(int id) throws BugNotFoundException {
        if (bugsRepository.findById(id).isEmpty())
            throw new BugNotFoundException(id);

        bugsRepository.deleteById(id);
    }

    public void reportBug(Bug bug){
        bugsRepository.save(bug);
    }
}
