package com.example.cobot;

public class CountryData {
    private String country;
    private String totalCases;
    private String newCases;
    private String totalDeaths;
    private String newDeaths;
    private String totalRecovered;
    private String newRecovered;

    public CountryData(String name, String totalCases, String newCases, String totalDeaths, String newDeaths, String totalRecovered, String newRecouvered) {
        country = name;
        this.totalCases = totalCases;
        this.newCases = newCases;
        this.totalDeaths = totalDeaths;
        this.newDeaths = newDeaths;
        this.totalRecovered = totalRecovered;
        this.newRecovered = newRecouvered;
    }

    @Override
    public String toString() {
        return "CountryData{" +
                "name='" + country + '\'' +
                ", totalCases='" + totalCases + '\'' +
                ", newCases='" + newCases + '\'' +
                ", totalDeaths='" + totalDeaths + '\'' +
                ", newDeaths='" + newDeaths + '\'' +
                ", totalRecovered='" + totalRecovered + '\'' +
                ", newRecovered='" + newRecovered + '\'' +
                '}';
    }

    public String getName() {
        return country;
    }

    public void setName(String name) {
        this.country = name;
    }

    public String getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases;
    }

    public String getNewCases() {
        return newCases;
    }

    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    public String getTotalDeaths() {
        return totalDeaths;
    }

    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    public String getNewDeaths() {
        return newDeaths;
    }

    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths;
    }

    public String getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(String totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public String getNewRecovered() {
        return newRecovered;
    }

    public void setNewRecovered(String newRecovered) {
        this.newRecovered = newRecovered;
    }
}
