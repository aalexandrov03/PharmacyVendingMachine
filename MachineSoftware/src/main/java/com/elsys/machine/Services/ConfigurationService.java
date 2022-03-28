package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.ConfigurationRepository;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Models.RouterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ConfigurationService {
    private final MedicinesRepository medicinesRepository;
    private final ConfigurationRepository configurationRepository;
    private final String configFileName;

    @Autowired
    public ConfigurationService(MedicinesRepository medicinesRepository,
                                ConfigurationRepository configurationRepository) {
        this.medicinesRepository = medicinesRepository;
        this.configurationRepository = configurationRepository;
        this.configFileName = "configuration.yaml";
    }

    public boolean getStatus() throws IOException {
        return configurationRepository.read(configFileName).isStatus();
    }

    public void setStatus(boolean status) throws IOException {
        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setStatus(status);
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public List<Mapping> getMapping() throws IOException {
        return configurationRepository.read(configFileName).getMapping();
    }

    public void setMapping(List<Mapping> newMapping) throws IOException, IllegalArgumentException {
        if (!checkMapping(newMapping))
            throw new IllegalArgumentException("Invalid mapping provided!");

        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setMapping(newMapping);
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public Configuration getConfiguration() throws IOException {
        return configurationRepository.read(configFileName);
    }

    public RouterSettings getRouterSettings() throws IOException {
        return configurationRepository.read(configFileName).getSettings();
    }

    public void setRouterSettings(RouterSettings settings) throws IOException, IllegalArgumentException {
        if (!checkRouterSettings(settings))
            throw new IllegalArgumentException("Invalid router settings provided!");

        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setSettings(settings);
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public void deleteRouterSettings() throws IOException {
        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setSettings(null);
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public void deleteRouterMapping() throws IOException {
        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setMapping(new ArrayList<>());
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public void setServerAddress(String address) throws IOException {
        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setServer_address(
                Base64.getEncoder().encodeToString(address.getBytes())
        );
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }

    public String getServerAddress() throws IOException {
        return new String(Base64.getDecoder().
                decode(configurationRepository.read(configFileName).getServer_address()));
    }

    private boolean checkRouterSettings(RouterSettings routerSettings) {
        return routerSettings.getRows() >= 0 && routerSettings.getColumns() >= 0 &&
                routerSettings.getDistSlots() >= 0 && routerSettings.getDistRows() >= 0 &&
                routerSettings.getStepsPerRev() >= 0 && routerSettings.getDistPerRev() >= 0;
    }

    private boolean checkMapping(List<Mapping> mappings) {
        List<Medicine> availableMedicines = StreamSupport.stream(
                medicinesRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        List<Medicine> mappedMedicines = mappings.stream()
                .map(mapping -> {
                    Optional<Medicine> medicine
                            = medicinesRepository.findMedicineByName(mapping.getMedicineName());

                    if (medicine.isEmpty())
                        return null;

                    return medicine.get();
                }).collect(Collectors.toList());

        List<Long> invalidSlotIDs = mappings.stream()
                .map(Mapping::getSlotID).filter(id -> id < 0)
                .collect(Collectors.toList());

        return availableMedicines.size() == mappedMedicines.size() &&
                availableMedicines.containsAll(mappedMedicines) &&
                invalidSlotIDs.isEmpty();
    }
}
