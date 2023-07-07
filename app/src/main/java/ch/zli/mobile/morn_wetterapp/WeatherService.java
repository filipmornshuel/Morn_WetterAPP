package ch.zli.mobile.morn_wetterapp;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {
    private String apiKey = "4b90aa4b9b7faffcfae5b982f1ae1d45";
    private String language = "de";

    private String cityName;
    private double temp;
    private String img;
    private String weatherDesc;
    private double airPressure;

    public WeatherService (){
    }

    public WeatherData getWeatherData(String cityNameWeather){
        WeatherData weatherData = new WeatherData();
                try {
                    URL url1 = new URL("https://api.openweathermap.org/data/2.5/weather?q="+cityNameWeather+"&APPID="+apiKey+"&units=metric"+"&lang="+language);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null){
                            response.append(line);
                        }
                        reader.close();

                        JSONObject jsonObject = new JSONObject(response.toString());
                        double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                        String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                        String iconId = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

                        //setTemp(temperature);
                        String iconUrl = "http://openweathermap.org/img/w/" + iconId +".png";


                        weatherData.setTemp(temperature);
                        weatherData.setWeatherDesc(weatherDesc);
                        weatherData.setImg(iconUrl);



                    }
                    connection.disconnect();
                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
                return weatherData;
    }


    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
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
