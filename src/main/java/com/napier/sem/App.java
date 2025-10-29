package com.napier.sem;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        World world = new World();
        world.connect();

        String continent = "Asia";

        List<Country> countries = world.getCountriesByContinent(continent);

        if (countries.isEmpty()) {
            System.out.println("No countries found for continent: " + continent);
        } else {
            System.out.printf("%-35s %15s%n", "Country", "Population");
            System.out.println("--------------------------------------------------------");
            for (Country c : countries) {
                System.out.printf("%-35s %15d%n", c.getName(), c.getPopulation());
            }
        }

        world.disconnect(); //  safely close connection
    }
}
