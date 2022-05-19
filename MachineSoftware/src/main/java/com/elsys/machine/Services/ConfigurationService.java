package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.ConfigurationRepository;
import com.elsys.machine.DataAccess.MappingRepository;
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
    private final MappingRepository mappingRepository;
    private final String configFileName;

    @Autowired
    public ConfigurationService(MedicinesRepository medicinesRepository,
                                ConfigurationRepository configurationRepository,
                                MappingRepository mappingRepository) {
        this.medicinesRepository = medicinesRepository;
        this.configurationRepository = configurationRepository;
        this.mappingRepository = mappingRepository;
        this.configFileName = "/home/sasho/machine/configuration.yaml";
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

    public List<Mapping> getMapping() {
        return StreamSupport.stream(
                mappingRepository.findAll().spliterator(), false).collect(Collectors.toList());
    }

    public void setMapping(List<Mapping> newMapping) throws IOException, IllegalArgumentException {
        if (!checkMapping(newMapping))
            throw new IllegalArgumentException("Invalid mapping provided!");

        for (Mapping m : newMapping){
            Optional<Mapping> mapping = mappingRepository.findBySlotID(m.getSlotID());

            if (mapping.isEmpty())
                mapping = Optional.of(m);
            else
                mapping.get().setMedicineName(m.getMedicineName());

            mappingRepository.save(mapping.get());
        }
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

    public void deleteRouterMapping() {
        mappingRepository.deleteAll();
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

    private boolean checkMapping(List<Mapping> mappings) throws IOException{
        RouterSettings routerSettings;
        routerSettings = getRouterSettings();

        List<String> medNames = StreamSupport.stream(
                medicinesRepository.findAll().spliterator(), false
        ).map(Medicine::getName).collect(Collectors.toList());

        List<String> mappingNames = StreamSupport.stream(
                mappingRepository.findAll().spliterator(), false
        ).map(Mapping::getMedicineName).collect(Collectors.toList());

        List<Long> slotIds = StreamSupport.stream(
                mappingRepository.findAll().spliterator(), false
        ).map(Mapping::getSlotID).collect(Collectors.toList());

        int ids = routerSettings.getColumns() * routerSettings.getRows();

        for (Mapping mapping : mappings) {
            if (mapping.getSlotID() < 1 || mapping.getSlotID() > ids)
                return false;

            if (!medNames.contains(mapping.getMedicineName()))
                return false;

            if (mappingNames.contains(mapping.getMedicineName()))
                return false;

            if (slotIds.contains(mapping.getSlotID()))
                return false;
        }

        return true;
    }
}
