package com.napier.sem;

public class Country {
    private String name;
    private String code;
    private String continent;
    private String region;
    private int population;  // changed to int

    // constructor matching database query
    public Country(String name, String code, String continent, String region, int population) {
        this.name = name;
        this.code = code;
        this.continent = continent;
        this.region = region;
        this.population = population;
    }

    // getters
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getContinent() { return continent; }
    public String getRegion() { return region; }
    public int getPopulation() { return population; }

    @Override
    public String toString() {
        return name + " (" + code + "), " + continent + "/" + region + " - pop: " + population;
    }
}