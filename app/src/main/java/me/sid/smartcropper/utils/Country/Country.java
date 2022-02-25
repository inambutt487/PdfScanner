package me.sid.smartcropper.utils.Country;

public class Country {


    private String code;
    private String name;
    private String country;
    private String language;
    private String language_country;

    public Country(String code, String name, String country, String language, String language_country) {
        this.code = code;
        this.name = name;
        this.country = country;
        this.language = language;
        this.language_country = language_country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage_country() {
        return language_country;
    }

    public void setLanguage_country(String language_country) {
        this.language_country = language_country;
    }
}
