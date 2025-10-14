package com.napier.sem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class World {
    private Connection connection;

    // ✅ Connect to the MySQL database
    public void connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/world?useSSL=false&allowPublicKeyRetrieval=true";
            String user = "root";
            String password = "root";


            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to the database successfully.");
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
            connection = null;
        }
    }

    // ✅ Disconnect from database
    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Disconnected from database.");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error closing connection: " + e.getMessage());
        }
    }

    // ✅ Fetch countries by continent
    public List<Country> getCountriesByContinent(String continent) {
        List<Country> countries = new ArrayList<>();

        if (connection == null) {
            System.out.println("❌ No database connection.");
            return countries;
        }

        try {
            String query = "SELECT Name, Population FROM country WHERE Continent = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // Use constructor directly since your Country class doesn’t have setters
                Country c = new Country(rs.getString("Name"), rs.getInt("Population"));
                countries.add(c);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ Query failed: " + e.getMessage());
        }

        return countries;
    }
}
