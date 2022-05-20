package com.elsys.machine.DataAccess;

import com.elsys.machine.Models.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;

@Repository
@NoArgsConstructor
public class ConfigurationRepository {
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public synchronized void write(String filename, Configuration configuration) throws IOException {
        mapper.writeValue(new File(filename), configuration);
    }

    public Configuration read(String filename) throws IOException {
        return mapper.readValue(new File(filename), Configuration.class);
    }
}
