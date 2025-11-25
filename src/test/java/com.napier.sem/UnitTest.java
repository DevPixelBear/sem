package com.napier.sem;

import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class UnitTest {
    private List<Country> sampleCountries;

    @BeforeEach
    void setup() {
        sampleCountries = new ArrayList<>();
        sampleCountries.add(new Country("USA", "United States", "North America", "North America", 331000000));
        sampleCountries.add(new Country("CAN", "Canada", "North America", "North America", 38000000));
        sampleCountries.add(new Country("MEX", "Mexico", "North America", "Central America", 128000000));
    }

    @Test
    void testLimitListFewerThanSize() {
        List<Country> limited = App.limitList(sampleCountries, 2);
        assertEquals(2, limited.size());
        assertEquals("United States", limited.get(0).getCode());
        assertEquals("Canada", limited.get(1).getCode());
    }

    @Test
    void testLimitListMoreThanSize() {
        List<Country> limited = App.limitList(sampleCountries, 5);
        assertEquals(3, limited.size()); // returns full list if n > list size
        // Ensure that the result list is the same size as the input list
        assertEquals(sampleCountries.size(), limited.size());
    }

    @Test
    void testLimitListZero() {
        List<Country> limited = App.limitList(sampleCountries, 0);
        assertNotNull(limited);
        //return an empty list
        assertTrue(limited.isEmpty());
        assertEquals(0, limited.size());
    }

    @Test
    void testLimitListNull() {
        List<Country> limited = App.limitList(null, 2);
        assertNull(limited); // should return null if input list is null
    }

    //test for city class
    @Test
    void testingCityGetters() {
        City city = new City(1, "London", "GBR", "England", 9000000);

        assertEquals(1, city.getID());
        assertEquals("London", city.getName());
        assertEquals("GBR", city.getCountryCode());
        assertEquals("England", city.getDistrict());
        assertEquals(9000000, city.getPopulation());
    }

    //tests for country class
    @Test
    void testingCountryGetters() {
        Country c = new Country("United Kingdom", "GBR", "Europe", "British Isles", 67000000);

        assertEquals("United Kingdom", c.getName());
        assertEquals("GBR", c.getCode());
        assertEquals("Europe", c.getContinent());
        assertEquals("British Isles", c.getRegion());
        assertEquals(67000000, c.getPopulation());
    }

}


