package com.napier.sem;

import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        World world = new World();
        world.connect(); //make sure this exists now


        List<Country> result = world.getCountries("world", null);
        List<Country> result2 = world.getCountries("continent", "Europe");
        List<Country> result3 = world.getCountries("region", "Caribbean");

        System.out.println("All countries in the world:");
        System.out.printf("%-30s %-15s%n", "Name", "Population");
        for (Country c : result) {
            System.out.printf("%-30s %-15d%n", c.getName(), c.getPopulation());
        }

        System.out.println("\nCountries in Europe:");
        System.out.printf("%-30s %-15s%n", "Name", "Population");
        for (Country c : result2) {
            System.out.printf("%-30s %-15d%n", c.getName(), c.getPopulation());
        }

        System.out.println("\nCountries in Caribbean:");
        System.out.printf("%-30s %-15s%n", "Name", "Population");
        for (Country c : result3) {
            System.out.printf("%-30s %-15d%n", c.getName(), c.getPopulation());
        }


//        System.out.print("Enter a continent (e.g. Asia, Europe): ");
//        String continent = "Asia";

//        List<Country> countries = world.getCountriesByContinent(continent);

//        if (countries.isEmpty()) {
//            System.out.println("No countries found for continent: " + continent);
//        } else {
//            System.out.printf("%-35s %15s%n", "Country", "Population");
//            System.out.println("--------------------------------------------------------");
//            for (Country c : countries) {
//                System.out.printf("%-35s %15d%n", c.getName(), c.getPopulation());
//            }
//        }

        world.disconnect(); //safely close connection
    }
}
