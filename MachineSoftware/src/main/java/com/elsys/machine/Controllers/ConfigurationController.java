package com.elsys.machine.Controllers;

import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.RouterSettings;
import com.elsys.machine.Services.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<?> getStatus() {
        try {
            return ResponseEntity.ok().body(configurationService.getStatus());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setStatus(@RequestParam boolean value) {
        try {
            configurationService.setStatus(value);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/router/mapping")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getMapping() {
        return ResponseEntity.ok().body(configurationService.getMapping());
    }

    @PostMapping("/router/mapping")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setMapping(@RequestBody List<Mapping> mapping) {
        try {
            configurationService.setMapping(mapping);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/router/mapping")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRouterMapping() {
        configurationService.deleteRouterMapping();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/router/settings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getRouterSettings() {
        try {
            return ResponseEntity.ok().body(configurationService.getRouterSettings());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/router/settings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setRouterSettings(@RequestBody RouterSettings settings) {
        try {
            configurationService.setRouterSettings(settings);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/router/settings")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteRouterSettings() {
        try {
            configurationService.deleteRouterSettings();
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred!");
        }
    }

    @GetMapping("/server")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getServerAddress() {
        try {
            return ResponseEntity.ok().body(configurationService.getServerAddress());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred!");
        }
    }

    @PostMapping("/server")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setServerAddress(@RequestBody String server) {
        try {
            configurationService.setServerAddress(server.substring(1, server.length() - 1));
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error occurred!");
        }
    }
}
