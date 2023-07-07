package ch.zli.mobile.morn_wetterapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherUpdateService extends Service {
    private Handler mHandler;
    private String apiKey = "4b90aa4b9b7faffcfae5b982f1ae1d45";
    private String language = "de";
    private String city = "Genf";
    WeatherData weatherData = new WeatherData();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url1 = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID="+apiKey+"&units=metric"+"&lang="+language);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    reader.close();
                    JSONObject jsonObject = new JSONObject(response.toString());
                    double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                    String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    String iconId = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
                    weatherData.setTemp(temperature);
                    weatherData.setWeatherDesc(weatherDesc);
                    weatherData.setImg(iconId);
                }
                connection.disconnect();
            }catch (IOException | JSONException e){
                e.printStackTrace();
            }
            mHandler.postDelayed(this, 60*100);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}