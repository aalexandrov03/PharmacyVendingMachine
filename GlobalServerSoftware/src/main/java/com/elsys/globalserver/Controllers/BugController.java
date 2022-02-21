package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Models.Bug;
import com.elsys.globalserver.Exceptions.Bugs.BugNotFoundException;
import com.elsys.globalserver.Services.BugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bugs")
@CrossOrigin(origins = "*")
public class BugController {
    private final BugsService bugsService;

    @Autowired
    public BugController(BugsService bugsService) {
        this.bugsService = bugsService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBugs(){
        return ResponseEntity.ok().body(bugsService.getBugs());
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> reportBug(@RequestBody Bug bug){
        bugsService.reportBug(bug);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> clearBugs(@RequestBody List<Integer> bug_ids){
        try{
            bugsService.clearBugs(bug_ids);
            return ResponseEntity.ok().build();
        } catch(BugNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
