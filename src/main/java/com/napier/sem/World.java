package com.napier.sem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class World {
    private Connection connection;

    public World() {
        String host = System.getenv("DB_HOST");
        String port = System.getenv("DB_PORT");
        String db = System.getenv("DB_NAME");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASSWORD");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true";

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to MySQL at " + host + ":" + port);
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }

    // Method to get countries by continent, sorted by population
    public List<Country> getCountriesByContinent(String continent) {
        List<Country> countries = new ArrayList<>();
        String query = "SELECT Name, Population FROM country WHERE Continent = ? ORDER BY Population DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, continent);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("Name");
                int population = rs.getInt("Population");
                countries.add(new Country(name, population));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving countries: " + e.getMessage());
        }

        return countries;
    }
}
