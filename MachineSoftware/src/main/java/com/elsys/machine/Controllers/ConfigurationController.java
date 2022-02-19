package com.elsys.machine.Controllers;

import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.RouterSettings;
import com.elsys.machine.Services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {
    private final ConfigurationService configurationService;

    @Autowired
    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping("/status")
    public ResponseEntity<?> getStatus(){
        try {
            return ResponseEntity.ok().body(configurationService.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/status")
    public ResponseEntity<?> setStatus(@RequestParam boolean value){
        try {
            configurationService.setStatus(value);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/router/mapping")
    public ResponseEntity<?> getMapping(){
        try {
            return ResponseEntity.ok().body(configurationService.getMapping());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/router/mapping")
    public ResponseEntity<?> setMapping(@RequestBody List<Mapping> mapping){
        try {
            configurationService.setMapping(mapping);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/router/mapping")
    public ResponseEntity<?> deleteRouterMapping(){
        try {
            configurationService.deleteRouterMapping();
            return ResponseEntity.ok().build();
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occured!");
        }
    }

    @GetMapping("/router/settings")
    public ResponseEntity<?> getRouterSettings(){
        try{
            return ResponseEntity.ok().body(configurationService.getRouterSettings());
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/router/settings")
    public ResponseEntity<?> setRouterSettings(@RequestBody RouterSettings settings){
        try {
            configurationService.setRouterSettings(settings);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occured!");
        }
    }

    @DeleteMapping("/router/settings")
    public ResponseEntity<?> deleteRouterSettings(){
        try {
            configurationService.deleteRouterSettings();
            return ResponseEntity.ok().build();
        } catch(IOException e){
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occured!");
        }
    }
}
