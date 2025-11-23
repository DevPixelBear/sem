package com.napier.sem;

public class City {
    private int ID;
    private String name;
    private String countryCode;
    private String district;
    private int population;

    public City(int ID, String name, String countryCode, String district, int population) {
        this.ID = ID;
        this.name = name;
        this.district = district;
        this.countryCode = countryCode;
        this.population = population;
    }


    // getters
    public int getID() { return this.ID; }
    public String getName() { return name; }
    public String getCountryCode() { return countryCode; }
    public String getDistrict() { return district; }
    public int getPopulation() { return population; }

}
