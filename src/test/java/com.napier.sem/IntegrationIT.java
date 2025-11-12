package com.napier.sem;

import org.junit.jupiter.api.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationIT {

    World world;

    @BeforeAll
    void waitForDb() throws InterruptedException {
        world = new World();

        int retries = 15; // give a bit more time if DB is slow
        int delay = 2000; // 2 seconds

        while (retries > 0) {
            world.connect();
            if (world.isConnected()) {
                System.out.println("Database is ready!");
                return;
            }
            System.out.println("Waiting for DB to start...");
            retries--;
            Thread.sleep(delay);
        }

        fail("Database did not start in time, skipping integration tests.");
    }

    @AfterAll
    void cleanup() {
        if (world != null && world.isConnected()) {
            world.disconnect();
        }
    }

    @Test
    void testGetCountriesWorld() {
        List<Country> countries = world.getCountries("world", null);
        assertNotNull(countries);
        assertFalse(countries.isEmpty(), "Should return at least some countries");

        // Optional: only check this if your DB is seeded correctly
        assertEquals("United States", countries.get(0).getName());
    }

    @Test
    void testTopCountriesIntegration() {
        List<Country> top3 = App.limitList(world.getCountries("world", null), 3);
        assertEquals(3, top3.size(), "Top list should have 3 countries");
        assertTrue(top3.get(0).getPopulation() >= top3.get(1).getPopulation(),
                "Countries should be sorted by population");
    }
}
