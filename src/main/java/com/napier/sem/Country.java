package com.napier.sem;

public class Country {
    private String name;
    private int population;

    //putting data from database into class variables
    public Country(String name, int population) {
        this.name = name;
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }
}
