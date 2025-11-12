package com.napier.sem;

import org.junit.jupiter.api.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class IntegrationIT {

    @BeforeAll
    static void CheckForConnection() {
        World world = new World();
        world.connect();

        if (!world.isConnected()) {
            System.out.println("Skipping DB tests: no connection");
        }
    }

    @Test
    void testGetCountriesWorld() {
        World world = new World();
        world.connect();

        List<Country> countries = world.getCountries("world", null);
        assertNotNull(countries);
        assertTrue(countries.size() > 0); // should return at least some countries
        assertEquals("United States", countries.get(0).getName()); // if your DB is ordered by population

        world.disconnect();
    }

    @Test
    void testTopCountriesIntegration() {
        World world = new World();
        world.connect();

        List<Country> top3 = App.limitList(world.getCountries("world", null), 3);
        assertEquals(3, top3.size());
        assertTrue(top3.get(0).getPopulation() >= top3.get(1).getPopulation());

        world.disconnect();
    }


}
