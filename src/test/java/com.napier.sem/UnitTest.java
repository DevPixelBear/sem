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
        assertEquals("United States", limited.get(0).getName());
        assertEquals("Canada", limited.get(1).getName());
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
}

