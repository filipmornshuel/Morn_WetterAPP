package ch.zli.mobile.morn_wetterapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity{

    private SearchView searchView;
    private TextView cityName;
    private TextView celsiusView;
    private ImageView weatherConditionImg;
    private TextView weatherConditionDesc;
    private TextView airPressureView;
    private TextView airPressureField;

    private SensorManager sensorManager;
    private Sensor barometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        cityName = findViewById(R.id.cityField);
        celsiusView = findViewById(R.id.celsiusField);
        weatherConditionImg = findViewById(R.id.weatherConditionImg);
        weatherConditionDesc = findViewById(R.id.weatherConditionDesc);
        airPressureField = findViewById(R.id.airpressureVar);
        airPressureView = findViewById(R.id.airpressureField);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String city) {
                getWeatherData(city);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //sensorManager.registerListener(MainActivity.this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
                    double airPressure = sensorEvent.values[0];
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        });
    }

    private void getWeatherData(String cityNameWeather){
        String apiKey = "4b90aa4b9b7faffcfae5b982f1ae1d45";
        String countryCode = "CH";
        String language = "de";

        //OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/3.0/weather?q="+cityNameWeather+","+countryCode+"&APPID="+apiKey+"&units=metric";

        /*
        Request request = new Request.Builder()
                .url(url)
                .build();


         */

        /*
        try {
            //Response response = client.newCall(request).execute();
            URL url1 = new URL("https://api.openweathermap.org/data/2.5/weather?q="+cityNameWeather+","+countryCode+"&APPID="+apiKey+"&units=metric");
            HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK){
                //String jsonData = response.body().string();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null){
                    response.append(line);
                }
                reader.close();

                //try {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                    String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                    String cityTrue = jsonObject.getJSONObject("alerts").getString("sender_name");

                    WeatherData weatherData = new WeatherData();
                    weatherData.setTemp(temperature);
                    weatherData.setWeatherDesc(weatherDesc);
                    runOnUiThread(() -> {
                        cityName.setText(cityNameWeather);
                        celsiusView.setText((int) temperature);
                        weatherConditionDesc.setText(weatherDesc);

                    });
                //}catch (JSONException e){
                    //e.printStackTrace();
                //}
            } else {
                //Fehlerverarbeitung
            }

            connection.disconnect();
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }


         */


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Response response = client.newCall(request).execute();
                    URL url1 = new URL("https://api.openweathermap.org/data/2.5/weather?q="+cityNameWeather+"&APPID="+apiKey+"&units=metric"+"&lang="+language);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK){
                        //String jsonData = response.body().string();
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
                        //String iconId=jsonObject.getJSONObject("main").getString("icon");

                        WeatherData weatherData = new WeatherData();
                        weatherData.setTemp(temperature);
                        weatherData.setWeatherDesc(weatherDesc);
                        weatherData.setImg(iconId);

                        String iconUrl = "http://openweathermap.org/img/w/" + iconId +".png";


                        //ImageView imageView = new ImageView(new URL("http://openweathermap.org/img/w/" + iconId +".png"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cityName.setText(cityNameWeather);
                                celsiusView.setText(String.valueOf(temperature) + "° C");
                                weatherConditionDesc.setText(weatherDesc);
                                //weatherConditionImg.setImageResource();
                                Picasso.get().load(iconUrl).into(weatherConditionImg);
                            }
                        });
                    } else {
                        //Fehlerverarbeitung
                    }

                    connection.disconnect();
                }catch (IOException | JSONException e){
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }




}