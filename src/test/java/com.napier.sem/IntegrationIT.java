package com.napier.sem;

import org.junit.jupiter.api.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IntegrationIT {

    World world;
    // Define constants for clarity in the waiting logic
    static final int MAX_RETRIES = 15;
    static final int DELAY_MS = 2000;
    @BeforeAll
    void waitForDb() throws InterruptedException {
        world = new World();

        int retries = MAX_RETRIES; // give a bit more time if DB is slow
        //int delay = 2000; // 2 seconds

        while (retries > 0) {
            world.connect();
            if (world.isConnected()) {
                System.out.println("Database is ready!");
                // Seed the database with known data here if needed for specific assertions
                // world.seedTestData();
                return;
            }
            System.out.println("Waiting for DB to start...");
            retries--;
            // Use TimeUnit for explicit clarity
            TimeUnit.MILLISECONDS.sleep(DELAY_MS);
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

        // We ensure "United States" is present *somewhere* in the list if expected.
        boolean foundUS = countries.stream().anyMatch(c -> "United States".equals(c.getName()));
        assertTrue(foundUS, "The 'United States' should be present in the list of countries.");
    }

    @Test
    void testTopCountriesIntegration() {
        List<Country> countries = world.getCountries("world", null);
        List<Country> top3 = App.limitList(countries, 3);
        assertEquals(3, top3.size(), "Top list should have 3 countries");

        // Assuming getCountries returns sorted data or limitList sorts it
        assertTrue(top3.get(0).getPopulation() >= top3.get(1).getPopulation(),
                "Countries should be sorted by population (descending)");

        // find the real max population in the full list and verify it matches the top result
        long maxPopulationInDB = countries.stream()
                .mapToLong(Country::getPopulation)
                .max()
                //get long literal
                .orElse(0L);
        assertEquals(maxPopulationInDB, top3.get(0).getPopulation(), "Top result should match the maximum population in the database.");
    }
}
