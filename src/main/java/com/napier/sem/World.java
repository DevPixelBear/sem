package com.napier.sem;

import com.mysql.cj.protocol.Resultset;
import java.util.ArrayList;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.HashMap;


public class World {
    private Connection connection;

    //Connect to the MySQL database
    public void connect() {
        try {
            // add environment variables to create a dynamic connection
            String host = System.getenv().getOrDefault("DB_HOST", "127.0.0.1");
            String url = "jdbc:mysql://" + host + ":3306/world";
            String user = "root";
            String password = "root";

            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            connection = null;
        }
    }

    //Disconnect from database
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    //asking if there is a connection there
    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    // Public method for accessing city data specifically
    public List<City> getCities(String type, String value) {
        String sql;
        Object[] params;

        switch (type.toLowerCase()) {
            // Query for all cities in the world organised by largest population to smallest
            case "world":
                sql = "SELECT * FROM city ORDER BY Population DESC";
                params = new Object[]{};
                break;

            // Query for all cities in a continent organised by largest population to smallest
            case "continent":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Continent = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            // Query for all cities in a region organised by largest population to smallest
            case "region":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Region = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            // Query for all the cities in a country organised by largest population to smallest
            case "country":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Name = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            // Query for all the cities in a district organised by largest population to smallest
            case "district":
                sql = "SELECT city.* FROM city " +
                        "WHERE city.District = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            // Default return empty list with an error message
            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        try {
            // Run query and return results
            ResultSet resultset = runQuery(sql, params);
            return buildCities(resultset);
        }
        catch (SQLException e) {
            // Print error message if query fails
            System.err.println("Query failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Public method for accessing data for all capital cities specifically
    public List<City> getTopNCapitalCities(String type, String value, int n) {
        String sql;
        Object[] params;

        switch (type.toLowerCase()) {
            // Query for all capital cities in the world organised by largest population to smallest
            case "continent":
                sql = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON country.Capital = city.ID " +
                        "WHERE country.Continent = ? " +
                        "ORDER BY city.Population DESC " +
                        "LIMIT ?" ;
                params = new Object[]{ value, n };
                break;

            // Query for all capital cities in a continent organised by largest population to smallest
            case "region":
                sql = "SELECT city.ID, city.Name, city.CountryCode, city.District, city.Population " +
                        "FROM city " +
                        "JOIN country ON country.Capital = city.ID " +
                        "WHERE country.Region = ? " +
                        "ORDER BY city.Population DESC " +
                        "LIMIT ?";
                params = new Object[]{ value, n };
                break;

            // Default return empty list with an error message
            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        // Array list to store results
        List<City> list = new ArrayList<>();

        // Connect to database if not already connected
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            // Execute query and store results in list
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new City(
                            rs.getInt("ID"),
                            rs.getString("Name"),
                            rs.getString("CountryName"),  // use countryCode field to store country name
                            rs.getString("District"),
                            rs.getInt("Population")
                    ));
                }
            }
        // Catch any SQL errors
        } catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        return list;
    }


    // Public method for accessing country data specifically
    public List<Country> getCountries(String type, String value) {
        String sql;
        Object[] params;

        switch (type.toLowerCase()) {
            // Query for all countries in the world organised by largest population to smallest
            case "world":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country ORDER BY Population DESC";
                params = new Object[]{};
                break;

            // Query for all countries in a continent organised by largest population to smallest
            case "continent":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country WHERE Continent = ? ORDER BY Population DESC";
                params = new Object[]{ value };
                break;

            // Query for all countries in a region organised by largest population to smallest
            case "region":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country WHERE Region = ? ORDER BY Population DESC";
                params = new Object[]{ value };
                break;

            // Query for all capital cities in a country organised by largest population to smallest
            case "capital":
                sql = """
            SELECT city.* FROM city
            JOIN country ON country.Capital = city.ID
            ORDER BY city.Population DESC
            LIMIT ?""";
                params = new Object[]{ value };
                break;

            // Default return empty list with an error message
            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        return runCountryQuery(sql, params);
    }

    // Helper method to build City objects from ResultSet
    public List<City> buildCities (ResultSet rs) throws SQLException {

        // Create an array list to store cities
        List<City> cities = new ArrayList<>();
        while (rs.next()) {
            cities.add(new City(

                    rs.getInt("ID"),
                    rs.getString("Name"),
                    rs.getString("CountryCode"),
                    rs.getString("District"),
                    rs.getInt("Population")
            ));
        }


        return cities;
    }
    // 1. Top N populated cities in a district
    public List<City> getTopCitiesByDistrict(String district, int limit) throws SQLException {
        String sql = """
        SELECT * FROM city
        WHERE District = ?
        ORDER BY Population DESC
        LIMIT ?""";

        if (!isConnected()) connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, district);
            stmt.setInt(2, limit);

            ResultSet rs = stmt.executeQuery();
            List<City> cities = new ArrayList<>();

            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }
            return cities;
        }
    }

    // 2. Top N populated capital cities
    public List<City> getTopNPopulatedCapitalCities(int N) throws SQLException {
        String sql = """
        SELECT city.* FROM city
        JOIN country ON country.Capital = city.ID
        ORDER BY city.Population DESC
        LIMIT ?
    """;

        if (!isConnected()) connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, N);
            ResultSet rs = stmt.executeQuery();

            List<City> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }

            return cities;
        }
    }


    // 3. All capital cities sorted by population (worldwide)
    public List<City> getAllCapitalCitiesByPopulation() throws SQLException {
        String sql = """
        SELECT city.* FROM city
        JOIN country ON country.Capital = city.ID
        ORDER BY city.Population DESC
    """;

        if (!isConnected()) connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            List<City> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }
            return cities;
        }
    }
    // 4. All capital cities in a continent sorted by population
    public List<City> getCapitalCitiesByContinent(String continent) throws SQLException {
        String sql = """
        SELECT city.* FROM city
        JOIN country ON country.Capital = city.ID
        WHERE country.Continent = ?
        ORDER BY city.Population DESC
    """;

        if (!isConnected()) connect();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();

            List<City> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }

            return cities;
        }
    }
    // 5. All capital cities in a region sorted by population
    public List<City> getCapitalCitiesByRegion(String region) throws SQLException {
        String sql = """
        SELECT city.* FROM city
        JOIN country ON country.Capital = city.ID
        WHERE country.Region = ?
        ORDER BY city.Population DESC
        """;

        if (!isConnected()) connect();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, region);
            ResultSet rs = stmt.executeQuery();
            List<City> cities = new ArrayList<>();
            while (rs.next()) {
                cities.add(new City(
                        rs.getInt("ID"),
                        rs.getString("Name"),
                        rs.getString("CountryCode"),
                        rs.getString("District"),
                        rs.getInt("Population")
                ));
            }

            return cities;
        }
    }


    // Public method for accessing population statistics specifically
    public List<PopulationStat> getPopulationStat(String type, String value) {
        String sql = "";

        switch (type.toLowerCase()) {

            // Query for accessing population statistics for each continent
            case "continent":
                sql =
                        "SELECT c.Continent AS Name, " +
                                "SUM(c.Population) AS TotalPopulation, " +
                                "SUM(ci.Population) AS CityPopulation, " +
                                "(SUM(c.Population) - SUM(ci.Population)) AS NonCityPopulation " +
                                "FROM country c " +
                                "LEFT JOIN city ci ON ci.CountryCode = c.Code " +
                                "WHERE c.Continent = ? " +
                                "GROUP BY c.Continent";
                break;

            // Query for accessing population statistics for each region
            case "region":
                sql =
                        "SELECT c.Region AS Name, " +
                                "SUM(c.Population) AS TotalPopulation, " +
                                "SUM(ci.Population) AS CityPopulation, " +
                                "(SUM(c.Population) - SUM(ci.Population)) AS NonCityPopulation " +
                                "FROM country c " +
                                "LEFT JOIN city ci ON ci.CountryCode = c.Code " +
                                "WHERE c.Region = ? " +
                                "GROUP BY c.Region";
                break;

            // Query for accessing population statistics for each country
            case "country":
                sql =
                        "SELECT c.Name AS Name, " +
                                "c.Population AS TotalPopulation, " +
                                "(SELECT SUM(ci.Population) FROM city ci WHERE ci.CountryCode = c.Code) AS CityPopulation, " +
                                "(c.Population - (SELECT SUM(ci.Population) FROM city ci WHERE ci.CountryCode = c.Code)) AS NonCityPopulation " +
                                "FROM country c " +
                                "WHERE c.Name = ?";
                break;

            // Default return empty list with an error message
            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        // Array list to store results
        List<PopulationStat> list = new ArrayList<>();

        // Connect to database if not already connected
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, value);

            // Execute query and store results in list
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new PopulationStat(
                            rs.getString("Name"),
                            rs.getLong("TotalPopulation"),
                            rs.getLong("CityPopulation"),
                            rs.getLong("NonCityPopulation")
                    ));
                }
            }
        }
        // Catch any SQL errors
        catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        return list;
    }




    private ResultSet runQuery (String query, Object[] params)
    {


        try{
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            ResultSet resultSet;
            resultSet = statement.executeQuery();

            return resultSet;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Country> runCountryQuery(String sql, Object... params) {
        List<Country> list = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Country(
                            rs.getString("Name"),
                            rs.getString("Code"),
                            rs.getString("Continent"),
                            rs.getString("Region"),
                            rs.getInt("Population")
                    ));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
        }

        return list;
    }

}
