package com.napier.sem;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        // Create a scanner for user input and a World object to manage database access
        Scanner scanner = new Scanner(System.in);
        World world = new World();

        // Establish a connection to the MySQL database
        world.connect();

        int choice;
        do {
            // Main menu - presents user with all available options for querying the database
            System.out.println("\n=== World Database Menu ===");
            System.out.println("1. All countries in the world (by population)");
            System.out.println("2. All countries in a continent (by population)");
            System.out.println("3. All countries in a region (by population)");
            System.out.println("4. Top N populated countries in the world");
            System.out.println("5. Top N populated countries in a continent");
            System.out.println("6. Top N populated countries in a region");
            System.out.println("7. Top N populated cities in the world");
            System.out.println("8. Top N populated cities in a continent");
            System.out.println("9. Top N populated cities in a country");
            System.out.println("10. Top N populated cities in a region");
            System.out.println("11. All cities in the world");
            System.out.println("12. All cities in a country");
            System.out.println("13. All cities in a region");
            System.out.println("14. All cities in a district");
            System.out.println("15. All cities in a continent");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            // Validate input so non-integer values don't crash the program
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a number: ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine();

            // Execute the user’s selected option
            switch (choice) {
                case 1:
                    // Display all countries worldwide ordered by population
                    showCountries(world.getCountries("world", null));
                    break;

                case 2:
                    // Display all countries in a specific continent
                    System.out.print("Enter continent name (e.g. Asia): ");
                    String continent = scanner.nextLine();
                    showCountries(world.getCountries("continent", continent));
                    break;

                case 3:
                    // Display all countries in a specific region
                    System.out.print("Enter region name (e.g. Caribbean): ");
                    String region = scanner.nextLine();
                    showCountries(world.getCountries("region", region));
                    break;

                case 4:
                    // Display the top N countries worldwide by population
                    System.out.print("Enter number of top countries: ");
                    int topCountriesInWorld = getInt(scanner);
                    showCountries(limitList(world.getCountries("world", null), topCountriesInWorld));
                    break;

                case 5:
                    // Display the top N countries in a given continent
                    System.out.print("Enter continent name (e.g. Europe): ");
                    String countryCont = scanner.nextLine();
                    System.out.print("Enter number of top countries: ");
                    int topCont = getInt(scanner);
                    showCountries(limitList(world.getCountries("continent", countryCont), topCont));
                    break;

                case 6:
                    // Display the top N countries in a given region
                    System.out.print("Enter region name (e.g. Caribbean): ");
                    String countryReg = scanner.nextLine();
                    System.out.print("Enter number of top countries: ");
                    int topRegInCountry = getInt(scanner);
                    showCountries(limitList(world.getCountries("region", countryReg), topRegInCountry));
                    break;

                case 7:
                    // Display the top N cities in the world
                    System.out.print("Enter number of top cities: ");
                    int topCitiesInWorld = getInt(scanner);
                    showCities(limitList(world.getCities("world", null), topCitiesInWorld));
                    break;

                case 8:
                    // Display the top N cities in a given continent
                    System.out.print("Enter continent name (e.g. Europe): ");
                    String contCity = scanner.nextLine();
                    System.out.print("Enter number of top cities: ");
                    int topCitiesInContinent = getInt(scanner);
                    showCities(limitList(world.getCities("continent", contCity), topCitiesInContinent));
                    break;

                case 9:
                    // Display the top N cities in a given country
                    System.out.print("Enter country name (e.g. France): ");
                    String countryCity = scanner.nextLine();
                    System.out.print("Enter number of top cities: ");
                    int topCitiesInCountry = getInt(scanner);
                    showCities(limitList(world.getCities("country", countryCity), topCitiesInCountry));
                    break;

                case 10:
                    // Display the top N cities in a given region
                    System.out.print("Enter region name (e.g. Caribbean): ");
                    String regCity = scanner.nextLine();
                    System.out.print("Enter number of top cities: ");
                    int topCitiesInReg = getInt(scanner);
                    showCities(limitList(world.getCities("region", regCity), topCitiesInReg));
                    break;

                case 11:
                    // Display all cities in the world, in no particular order
                    showCities(world.getCities("world", null));
                    break;

                case 12:
                    // Display all cities in a city
                    System.out.print("Enter a country name: ");
                    String countryUserInput = scanner.nextLine();
                    showCities(world.getCities("country", countryUserInput));
                    break;

                case 13:
                    //display all cities in a region
                    System.out.print("Enter a region: ");
                    String regionUserInput = scanner.nextLine();
                    showCities(world.getCities("region", regionUserInput));
                    break;

                case 14:
                    //display all cities in a district
                    System.out.print("Enter a district: ");
                    String districtUserInput = scanner.nextLine();
                    showCities(world.getCities("district", districtUserInput));
                    break;

                case 15:
                    //display all cities in a continent
                    System.out.print("Enter a continent: ");
                    String continentUserInput = scanner.nextLine();
                    showCities(world.getCities("continent", continentUserInput));
                    break;


                case 0:
                    // Clean exit
                    System.out.println("Exiting program...");
                    break;

                default:
                    // Handles any invalid menu selection
                    System.out.println("Invalid choice. Try again.");
            }

        } while (choice != 0); // Loop continues until user chooses to exit

        world.disconnect();
        scanner.close();
    }

    private static void showCities(List<City> cities) {
        if (cities == null || cities.isEmpty()) {
            System.out.println("No cities found.");
            return;
        }

        System.out.printf("%-5s %-35s %-20s %-25s %-15s%n",
                "ID" , "Name", "District", "Code", "Population");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (City c : cities) {
            System.out.printf("%-5s %-35s %-20s %-25s %-15d%n",
                    c.getID(), c.getName(), c.getDistrict(), c.getCountryCode(), c.getPopulation());
        }

        System.out.println("\nTotal results: " + cities.size());
    }

    // Displays a list of Country objects in a formatted table
    private static void showCountries(List<Country> countries) {
        if (countries == null || countries.isEmpty()) {
            System.out.println("No countries found.");
            return;
        }

        System.out.printf("%-5s %-35s %-20s %-25s %-15s%n",
                "Code", "Name", "Continent", "Region", "Population");
        System.out.println("----------------------------------------------------------------------------------------------");

        for (Country c : countries) {
            System.out.printf("%-5s %-35s %-20s %-25s %-15d%n",
                    c.getCode(), c.getName(), c.getContinent(), c.getRegion(), c.getPopulation());
        }

        System.out.println("\nTotal results: " + countries.size());
    }

    // Repeatedly prompts user until a valid integer is entered
    private static int getInt(Scanner scanner) {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    // Returns the first N elements from the given list
    // If N exceeds the list size, returns the full list instead
    public static <T> List<T> limitList(List<T> list, int n) {
        if (list == null || list.isEmpty() || n >= list.size()) {
            return list;
        }
        return list.subList(0, n);
    }
}
