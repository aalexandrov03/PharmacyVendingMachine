package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.ConfigurationRepository;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.RouterSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ConfigurationService {
    private final ConfigurationRepository configurationRepository;
    private final String configFileName;

    @Autowired
    public ConfigurationService(ConfigurationRepository configurationRepository) {
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

    public void setMapping(List<Mapping> newMapping) throws IOException {
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

    public void setRouterSettings(RouterSettings settings) throws IOException {
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

    public void deleteRouterMapping() throws IOException{
        Configuration configuration = configurationRepository.read(configFileName);
        configuration.setMapping(new ArrayList<>());
        configuration.setUpdate_date(LocalDateTime.now().toString());
        configurationRepository.write(configFileName, configuration);
    }
}
