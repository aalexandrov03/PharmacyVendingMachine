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
public class BugsService {
    private final BugRepository bugsRepository;

    @Autowired
    public BugsService(BugRepository bugsRepository) {
        this.bugsRepository = bugsRepository;
    }

    public List<Bug> getBugs() {
        Iterable<Bug> bugs = bugsRepository.findAll();
        return StreamSupport.stream(bugs.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void clearBugs(List<Integer> bug_ids) throws BugNotFoundException {
        for (int id: bug_ids){
            Optional<Bug> bug = bugsRepository.findById(id);
            if (bug.isEmpty())
                throw new BugNotFoundException(id);
        }
        bugsRepository.deleteAllById(bug_ids);
    }

    public void reportBug(Bug bug){
        bugsRepository.save(bug);
    }
}
