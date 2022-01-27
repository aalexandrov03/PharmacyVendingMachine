package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DB_Entities.Bug;
import com.elsys.globalserver.Exceptions.Bugs.BugNotFoundException;
import com.elsys.globalserver.Services.BugsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bugs")
public class BugsController {
    private final BugsService bugsService;

    @Autowired
    public BugsController(BugsService bugsService) {
        this.bugsService = bugsService;
    }

    @GetMapping()
    public ResponseEntity<?> getBugs(){
        return ResponseEntity.ok().body(bugsService.getBugs());
    }

    @PostMapping()
    public ResponseEntity<?> reportBug(@RequestBody Bug bug){
        bugsService.reportBug(bug);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping()
    public ResponseEntity<?> clearBugs(@RequestBody List<Integer> bug_ids){
        try{
            bugsService.clearBugs(bug_ids);
            return ResponseEntity.ok().build();
        } catch(BugNotFoundException e){
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
