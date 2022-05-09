package com.elsys.machine.Services;

import com.elsys.machine.DataAccess.ConfigurationRepository;
import com.elsys.machine.DataAccess.MappingRepository;
import com.elsys.machine.DataAccess.MedicinesRepository;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Models.RouterSettings;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigurationServiceTest {
    private ConfigurationService configurationService;
    private ConfigurationRepository configurationRepository;
    private MappingRepository mappingRepository;
    private List<Mapping> mappings;
    private Configuration testConfiguration;

    @BeforeAll
    void setUpBeforeAll() throws IOException {
        MedicinesRepository medicinesRepository = Mockito.mock(MedicinesRepository.class);
        configurationRepository = Mockito.mock(ConfigurationRepository.class);
        mappingRepository = Mockito.mock(MappingRepository.class);

        configurationService = new ConfigurationService(
                medicinesRepository,
                configurationRepository,
                mappingRepository
        );

        Medicine medicine = new Medicine();
        medicine.setId(1);
        medicine.setName("Analgin");
        medicine.setPrice(3.50);
        medicine.setNeedsPrescription(false);
        medicine.setAmount(5);

        Mapping mapping = new Mapping();
        mapping.setSlotID(1);
        mapping.setMedicineName("Analgin");

        mappings = new ArrayList<>();
        mappings.add(mapping);

        RouterSettings routerSettings = new RouterSettings();
        routerSettings.setColumns(7);
        routerSettings.setRows(2);
        routerSettings.setDistRows(80);
        routerSettings.setDistPerRev(100);
        routerSettings.setDistSlots(50);
        routerSettings.setStepsPerRev(6300);

        testConfiguration = new Configuration();
        testConfiguration.setStatus(true);
        testConfiguration.setUpdate_date(LocalDateTime.now().toString());
        testConfiguration.setSettings(routerSettings);
        testConfiguration.setServer_address(Base64.getEncoder().encodeToString("address".getBytes()));

        Mockito.when(configurationRepository.read(anyString())).thenReturn(testConfiguration);
        Mockito.when(mappingRepository.findAll()).thenReturn(mappings);
        Mockito.when(mappingRepository.save(any(Mapping.class))).then((mock) -> {
            //System.out.println(().getMedicineName());
            mappings.add(mock.getArgument(0));
            return null;
        });
        Mockito.when(medicinesRepository.findAll()).thenReturn(List.of(medicine));
        Mockito.when(medicinesRepository.findMedicineByName(medicine.getName())).thenReturn(Optional.of(medicine));
    }

    @Test
    @Order(1)
    void getStatus() throws IOException {
        assertTrue(configurationService.getStatus());
    }

    @Test
    @Order(2)
    void setStatus() throws IOException {
        ArgumentCaptor<Configuration> configurationArgumentCaptor
                = ArgumentCaptor.forClass(Configuration.class);


        configurationService.setStatus(false);
        Mockito.verify(configurationRepository)
                .write(anyString(), configurationArgumentCaptor.capture());

        assertFalse(configurationArgumentCaptor.getValue().isStatus());
    }

    @Test
    @Order(3)
    void getMapping() {
        List<Mapping> mapping = configurationService.getMapping();
        List<Mapping> expected = StreamSupport.stream(
                mappingRepository.findAll().spliterator(), false
        ).collect(Collectors.toList());

        assertTrue(mapping.containsAll(expected));
    }

    @Test
    @Order(4)
    void setMapping() throws IOException {
        Mapping testMapping1 = new Mapping();
        testMapping1.setMedicineName("Benalgin");
        testMapping1.setSlotID(2);

        try{
            configurationService.setMapping(List.of(testMapping1));
            fail();
        } catch (IllegalArgumentException ignored){}

        Mapping testMapping2 = new Mapping();
        testMapping2.setSlotID(5);
        testMapping2.setMedicineName("Analgin");

        configurationService.setMapping(List.of(testMapping2));

        assertTrue(mappings.contains(testMapping2));
    }

    @Test
    @Order(5)
    void getConfiguration() throws IOException {
        assertEquals(testConfiguration, configurationService.getConfiguration());
    }

    @Test
    @Order(6)
    void getRouterSettings() throws IOException {
        assertEquals(testConfiguration.getSettings(), configurationService.getRouterSettings());
    }

    @Test
    @Order(7)
    void setRouterSettings() throws IOException {
        ArgumentCaptor<Configuration> configurationArgumentCaptor
                = ArgumentCaptor.forClass(Configuration.class);

        RouterSettings routerSettings1 = new RouterSettings();
        routerSettings1.setColumns(7);
        routerSettings1.setRows(2);
        routerSettings1.setDistRows(80);
        routerSettings1.setDistPerRev(100);
        routerSettings1.setDistSlots(50);
        routerSettings1.setStepsPerRev(6300);

        configurationService.setRouterSettings(routerSettings1);
        Mockito.verify(configurationRepository, Mockito.times(2))
                .write(anyString(), configurationArgumentCaptor.capture());

        assertEquals(routerSettings1, configurationArgumentCaptor.getValue().getSettings());

        RouterSettings routerSettings2 = new RouterSettings();
        routerSettings2.setColumns(7);
        routerSettings2.setRows(2);
        routerSettings2.setDistRows(80);
        routerSettings2.setDistPerRev(-12);
        routerSettings2.setDistSlots(50);
        routerSettings2.setStepsPerRev(6300);

        try{
            configurationService.setRouterSettings(routerSettings2);
            Mockito.verify(configurationRepository, Mockito.times(2))
                    .write(anyString(), configurationArgumentCaptor.capture());
            fail();
        } catch (IllegalArgumentException ignored){}
    }

    @Test
    @Order(8)
    void getServerAddress() throws IOException {
        assertEquals("address", configurationService.getServerAddress());
    }

    @Test
    @Order(9)
    void setServerAddress() throws IOException {
        String address = "new address";

        ArgumentCaptor<Configuration> configurationArgumentCaptor
                = ArgumentCaptor.forClass(Configuration.class);

        configurationService.setServerAddress(address);
        Mockito.verify(configurationRepository, Mockito.times(3))
                .write(anyString(), configurationArgumentCaptor.capture());

        assertEquals(Base64.getEncoder().encodeToString(address.getBytes())
                , configurationArgumentCaptor.getValue().getServer_address());
    }
}