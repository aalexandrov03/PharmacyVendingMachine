package com.elsys.globalserver.Controllers;

import com.elsys.globalserver.DataAccess.BugRepository;
import com.elsys.globalserver.DataAccess.PatientRepository;
import com.elsys.globalserver.Models.Bug;
import com.elsys.globalserver.Models.Patient;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BugControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private BugRepository bugRepository;
    @Autowired
    private PatientRepository patientRepository;

    private List<Bug> testBugs;

    @BeforeAll
    void setUpBeforeAll() {
        Patient patient = new Patient();
        patient.setFullName("Vasko Paligorov");
        patient.setEmail("vpal@gmail.com");
        patient.setPassword(
                new BCryptPasswordEncoder(10)
                        .encode("pass")
        );
        patientRepository.save(patient);
    }

    @BeforeEach
    void setUpBeforeEach() {
        Bug bug1 = new Bug();
        bug1.setTitle("Bug 1");
        bug1.setDescription("This is bug 1");

        Bug bug2 = new Bug();
        bug2.setTitle("Bug 2");
        bug2.setDescription("This is bug 2");

        testBugs = List.of(bug1, bug2);
        bugRepository.saveAll(List.of(bug1, bug2));
    }

    @AfterEach
    void tearDownAfterEach(){
        bugRepository.deleteAll();
    }

    @AfterAll
    void tearDownAfterAll(){
        patientRepository.deleteAll();
    }

    @Test
    void getBugs() {
        ResponseEntity<Bug[]> response = restTemplate
                .withBasicAuth("admin", "admin")
                .getForEntity("http://localhost:" + port + "/bugs", Bug[].class);

        if (response.getStatusCodeValue() != 200)
            fail();

        for (int i = 0; i < Objects.requireNonNull(response.getBody()).length; i++) {
            assertTrue(testBugs.contains(response.getBody()[i]));
        }
    }

    @Test
    void reportBug() {
        Bug bug = new Bug();
        bug.setTitle("new bug");
        bug.setDescription("this is a new bug");

        restTemplate
            .withBasicAuth("vpal@gmail.com", "pass")
            .postForEntity("http://localhost:" + port + "/bugs", bug, Bug.class);

        List<Bug> actualBugs = StreamSupport.stream(bugRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());

        assertTrue(actualBugs.contains(bug));
    }

    @Test
    void clearBug() {
        Bug bug = new Bug();
        bug.setTitle("new bug");
        bug.setDescription("this is a new bug");
        bug = bugRepository.save(bug);

        restTemplate
                .withBasicAuth("admin", "admin")
                .delete("http://localhost:" + port + "/bugs?id=" + bug.getId());

        List<Bug> actualBugs = StreamSupport
                .stream(bugRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        if (actualBugs.contains(bug))
            fail();
    }
}