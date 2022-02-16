package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.ConfigurationRepository;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
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
}
