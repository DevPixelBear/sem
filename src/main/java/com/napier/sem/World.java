package com.napier.sem;

import com.mysql.cj.protocol.Resultset;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

    //Fetch countries by continent
//    public List<Country> getCountriesByContinent(String continent) {
//        List<Country> countries = new ArrayList<>();
//
//        if (connection == null) {
//            System.out.println("No database connection.");
//            return countries;
//        }
//
//        try {
//            String query = "SELECT Name, Population FROM country WHERE Continent = ?";
//            PreparedStatement stmt = connection.prepareStatement(query);
//            stmt.setString(1, continent);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                // Use constructor directly since your Country class doesn’t have setters
//                Country c = new Country(rs.getString("Name"), rs.getInt("Population"));
//                countries.add(c);
//            }
//
//            rs.close();
//            stmt.close();
//        } catch (SQLException e) {
//            System.out.println("" +
//                    "" +
//                    "" +
//                    "" +
//                    "" +
//                    "" +
//                    "" +
//                    "" +
//                    "Query failed: " + e.getMessage());
//        }
//
//        return countries;
//    }


    public List<City> getCities(String type, String value) {
        String sql;
        Object[] params;

        switch (type.toLowerCase()) {
            case "world":
                sql = "SELECT * FROM city ORDER BY Population DESC";
                params = new Object[]{};
                break;

            case "continent":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Continent = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            case "region":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Region = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            case "country":
                sql = "SELECT city.* FROM city " +
                        "JOIN country ON city.CountryCode = country.Code " +
                        "WHERE country.Name = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            case "district":
                sql = "SELECT city.* FROM city " +
                        "WHERE city.District = ? ORDER BY city.Population DESC";
                params = new Object[]{ value };
                break;

            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        try {
            ResultSet resultset = runQuery(sql, params);
            return buildCities(resultset);
        }
        catch (SQLException e) {
            System.err.println("Query failed: " + e.getMessage());
            return new ArrayList<>();
        }
    }


    public List<Country> getCountries(String type, String value) {
        String sql;
        Object[] params;

        switch (type.toLowerCase()) {
            case "world":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country ORDER BY Population DESC";
                params = new Object[]{};
                break;

            case "continent":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country WHERE Continent = ? ORDER BY Population DESC";
                params = new Object[]{ value };
                break;

            case "region":
                sql = "SELECT Code, Name, Continent, Region, Population " +
                        "FROM country WHERE Region = ? ORDER BY Population DESC";
                params = new Object[]{ value };
                break;

            default:
                System.err.println("Invalid type: " + type);
                return new ArrayList<>();
        }

        return runCountryQuery(sql, params);
    }

    public List<City> buildCities (ResultSet rs) throws SQLException {

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
