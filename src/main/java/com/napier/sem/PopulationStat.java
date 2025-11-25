package com.napier.sem;

public class PopulationStat {
    public String name;
    public long total;
    public long inCities;
    public long notInCities;

    public PopulationStat(String name, long total, long inCities, long notInCities) {
        this.name = name;
        this.total = total;
        this.inCities = inCities;
        this.notInCities = notInCities;
    }
}
