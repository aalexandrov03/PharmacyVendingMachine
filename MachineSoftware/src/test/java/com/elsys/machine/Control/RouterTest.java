package com.elsys.machine.Control;

import com.elsys.machine.Control.Router.Router;
import com.elsys.machine.Control.Utils.Direction;
import com.elsys.machine.Control.Utils.Motor;
import com.elsys.machine.Control.Utils.RouteNode;
import com.elsys.machine.Models.Configuration;
import com.elsys.machine.Models.Mapping;
import com.elsys.machine.Models.Medicine;
import com.elsys.machine.Models.RouterSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouterTest {
    private List<RouteNode> expectedRoute;
    private Set<Medicine> testOrder;
    private Router router;

    @BeforeAll
    public void setUp(){
        testOrder = new LinkedHashSet<>();

        Medicine medicine1 = new Medicine();
        medicine1.setId(1);
        medicine1.setName("Analgin");
        medicine1.setNeedsPrescription(false);
        medicine1.setPrice(2.50);
        medicine1.setAmount(1);

        Medicine medicine2 = new Medicine();
        medicine2.setId(2);
        medicine2.setName("Benalgin");
        medicine2.setNeedsPrescription(false);
        medicine2.setPrice(3.50);
        medicine2.setAmount(1);

        testOrder.add(medicine1);
        testOrder.add(medicine2);

        RouterSettings settings = new RouterSettings();
        settings.setRows(2);
        settings.setColumns(7);
        settings.setDistPerRev(100);
        settings.setStepsPerRev(6300);
        settings.setDistSlots(55);
        settings.setDistRows(80);

        List<Mapping> mappings = new ArrayList<>(List.of(
                new Mapping("Analgin", 2),
                new Mapping("Benalgin", 4)
        ));

        Configuration configuration = new Configuration();
        configuration.setSettings(settings);
        configuration.setMapping(mappings);
        router = new Router(configuration);

        expectedRoute = new LinkedList<>(List.of(
                new RouteNode(Motor.Z, 630, Direction.UP),
                new RouteNode(Motor.X, 3465, Direction.RIGHT),
                new RouteNode(Motor.Z, 5040, Direction.UP),
                new RouteNode(Motor.X, 17325, Direction.RIGHT),
                new RouteNode(Motor.Z, 5040, Direction.DOWN),
                new RouteNode(Motor.X, 10395, Direction.LEFT),
                new RouteNode(Motor.Z, 5040, Direction.UP),
                new RouteNode(Motor.X, 10395, Direction.RIGHT),
                new RouteNode(Motor.Z, 5040, Direction.DOWN),
                new RouteNode(Motor.X, 20790, Direction.LEFT),
                new RouteNode(Motor.Z, 630, Direction.DOWN)
        ));
    }

    @Test
    void testCreateRoute(){
        List<RouteNode> actualRoute = router.createRoute(testOrder);
        assertTrue(compareRoutes(expectedRoute, actualRoute));
    }

    private boolean compareRoutes(List<RouteNode> expectedRoute,
                                   List<RouteNode> actualRoute){

        if (expectedRoute.size() != actualRoute.size())
            return false;

        for (int i = 0; i < expectedRoute.size(); i ++){
            if (!expectedRoute.get(i).equals(actualRoute.get(i)))
                return false;
        }

        return true;
    }
}
