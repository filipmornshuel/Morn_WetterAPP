package ch.zli.mobile.morn_wetterapp;

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

    public WeatherService() {
    }

    public WeatherData getWeatherData(String cityNameWeather) {
        WeatherData weatherData = new WeatherData();
        try {
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + cityNameWeather + "&APPID=" + apiKey + "&units=metric" + "&lang=" + language);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                JSONObject jsonObject = new JSONObject(response.toString());
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconId = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                String iconUrl = "http://openweathermap.org/img/w/" + iconId + ".png";
                weatherData.setTemp(temperature);
                weatherData.setWeatherDesc(weatherDesc);
                weatherData.setImg(iconUrl);
            }
            connection.disconnect();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return weatherData;
    }
}
