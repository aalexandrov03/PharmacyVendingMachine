package com.elsys.globalserver.Services;

import com.elsys.globalserver.DataAccess.BugRepository;
import com.elsys.globalserver.Exceptions.Bugs.BugNotFoundException;
import com.elsys.globalserver.Models.Bug;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BugServiceTest {
    private BugService bugService;
    private BugRepository bugRepository;
    private List<Bug> testBugs;

    @BeforeAll
    void setUp(){
        Bug bug1 = new Bug();
        bug1.setTitle("Bug 1");
        bug1.setDescription("This is bug 1");

        Bug bug2 = new Bug();
        bug2.setTitle("Bug 2");
        bug2.setDescription("This is bug 2");

        testBugs = List.of(bug1, bug2);

        bugRepository = Mockito.mock(BugRepository.class);
        Mockito.when(bugRepository.findAll()).thenReturn(testBugs);
        Mockito.doNothing().when(bugRepository).deleteById(Mockito.anyInt());
        Mockito.when(bugRepository.save(Mockito.any(Bug.class))).thenReturn(null);
        Mockito.when(bugRepository.findById(1)).thenReturn(Optional.of(bug1));
        bugService = new BugService(bugRepository);
    }

    @Test
    void getBugs() {
        List<Bug> bugs = bugService.getBugs();
        assertTrue(bugs.containsAll(testBugs));
    }

    @Test
    void clearBug() throws BugNotFoundException {
        ArgumentCaptor<Integer> integerArgumentCaptor
                = ArgumentCaptor.forClass(Integer.class);

        bugService.clearBug(1);

        Mockito.verify(bugRepository).deleteById(integerArgumentCaptor.capture());

        assertEquals(1, integerArgumentCaptor.getValue());
    }

    @Test
    void reportBug() {
        Bug bug = new Bug();
        bug.setTitle("New Bug");
        bug.setDescription("This is a new bug");

        ArgumentCaptor<Bug> bugArgumentCaptor
                = ArgumentCaptor.forClass(Bug.class);

        bugService.reportBug(bug);

        Mockito.verify(bugRepository).save(bugArgumentCaptor.capture());

        assertEquals(bug, bugArgumentCaptor.getValue());
    }
}