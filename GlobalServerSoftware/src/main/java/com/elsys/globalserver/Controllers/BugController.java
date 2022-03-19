package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.Models.Bug;
import com.elsys.globalserver.Exceptions.Bugs.BugNotFoundException;
import com.elsys.globalserver.Services.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bugs")
public class BugController {
    private final BugService bugService;

    @Autowired
    public BugController(BugService bugService) {
        this.bugService = bugService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getBugs(){
        return ResponseEntity.ok().body(bugService.getBugs());
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_DOCTOR')")
    public ResponseEntity<?> reportBug(@RequestBody Bug bug){
        bugService.reportBug(bug);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> clearBug(@RequestBody int bug_id){
        try{
            bugService.clearBug(bug_id);
            return ResponseEntity.ok().build();
        } catch(BugNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
