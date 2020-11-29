package com.example.cobot;

public class CountryData {
    private String country;
    private String totalCases;
    private String newCases;
    private String totalDeaths;
    private String newDeaths;
    private String totalRecovered;
    private String newRecovered;

    /**
     * A constructor for the country data relating to COVID-19
     * @param name the name of the country
     * @param totalCases the total number of cases in the country
     * @param newCases the number of new cases in the country
     * @param totalDeaths the total deaths in the country
     * @param newDeaths the number of new deaths in the country
     * @param totalRecovered the total number of people recovered from COVID-19
     * @param newRecouvered the number of new people recovered from COVID-19
     */
    public CountryData(String name, String totalCases, String newCases, String totalDeaths, String newDeaths, String totalRecovered, String newRecouvered) {
        country = name;
        this.totalCases = totalCases;
        this.newCases = newCases;
        this.totalDeaths = totalDeaths;
        this.newDeaths = newDeaths;
        this.totalRecovered = totalRecovered;
        this.newRecovered = newRecouvered;
    }

    /**
     * A function to turn the country data into a string
     * @return the country data as a string
     */
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

    /**
     * A getter for the name of the country
     * @return the name of the country
     */
    public String getName() {
        return country;
    }

    /**
     * A setter for the name of the country
     * @param name the name of the country
     */
    public void setName(String name) {
        this.country = name;
    }

    /**
     * A getter for the total number of cases
     * @return the total number of cases
     */
    public String getTotalCases() {
        return totalCases;
    }

    /**
     * A setter for the total number of cases
     * @param totalCases the total number of cases
     */
    public void setTotalCases(String totalCases) {
        this.totalCases = totalCases;
    }

    /**
     * A getter for the number of new cases
     * @return the number of new cases
     */
    public String getNewCases() {
        return newCases;
    }

    /**
     * A setter for the number of new cases
     * @param newCases the number of new cases
     */
    public void setNewCases(String newCases) {
        this.newCases = newCases;
    }

    /**
     * A getter for the total number of deaths
     * @return the total number of deaths
     */
    public String getTotalDeaths() {
        return totalDeaths;
    }

    /**
     * A setter for the total number of deaths
     * @param totalDeaths the total number of deaths
     */
    public void setTotalDeaths(String totalDeaths) {
        this.totalDeaths = totalDeaths;
    }

    /**
     * A getter for the number of new deaths
     * @return the number of new deaths
     */
    public String getNewDeaths() {
        return newDeaths;
    }

    /**
     * A setter for the number of new deaths
     * @param newDeaths the number of new deaths
     */
    public void setNewDeaths(String newDeaths) {
        this.newDeaths = newDeaths;
    }

    /**
     * A getter for the total number of people recovered
     * @return the total number of people recovered
     */
    public String getTotalRecovered() {
        return totalRecovered;
    }

    /**
     * A setter for the total number of people recovered
     * @param totalRecovered the total number of people recovered
     */
    public void setTotalRecovered(String totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    /**
     * A getter for the new amount of people recovered
     * @return the total new people recovered
     */
    public String getNewRecovered() {
        return newRecovered;
    }

    /**
     * A setter for the new people recovered
     * @param newRecovered the total amount of new people recovered
     */
    public void setNewRecovered(String newRecovered) {
        this.newRecovered = newRecovered;
    }
}
