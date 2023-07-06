package ch.zli.mobile.morn_wetterapp;

public class WeatherData {
    private String cityName;
    private double tmep;
    private String img;
    private String weatherDesc;
    private double airPressure;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTmep() {
        return tmep;
    }

    public void setTmep(double tmep) {
        this.tmep = tmep;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public double getAirPressure() {
        return airPressure;
    }

    public void setAirPressure(double airPressure) {
        this.airPressure = airPressure;
    }
}
