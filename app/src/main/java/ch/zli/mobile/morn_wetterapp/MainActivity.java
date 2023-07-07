package ch.zli.mobile.morn_wetterapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.media.Image;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity{

    private SearchView searchView;
    private TextView cityName;
    private TextView celsiusView;
    private ImageView weatherConditionImg;
    private TextView weatherConditionDesc;
    private TextView airPressureView;
    private TextView airPressureField;
    private EditText searchEditText;
    private SensorManager sensorManager;
    private Sensor barometerSensor;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("Wetterupdate".equals(intent.getAction())){
                String city = intent.getStringExtra("city");
                double temperature = intent.getDoubleExtra("temperature", 0.0);
                String weatherDesc = intent.getStringExtra("weatherDesc");
                String iconId = intent.getStringExtra("iconId");

                cityName.setText(city);
                celsiusView.setText(temperature + " ° C");
                weatherConditionDesc.setText(weatherDesc);
                String iconUrl = "http://openweathermap.org/img/w/" + iconId +".png";
                Picasso.get().load(iconUrl).into(weatherConditionImg);

                Toast.makeText(context, "Wetterdaten aktualisiert", Toast.LENGTH_LONG).show();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.cityField);
        celsiusView = findViewById(R.id.celsiusField);
        weatherConditionImg = findViewById(R.id.weatherConditionImg);
        weatherConditionDesc = findViewById(R.id.weatherConditionDesc);
        airPressureField = findViewById(R.id.airpressureVar);
        airPressureView = findViewById(R.id.airpressureField);

        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        barometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        getWeatherData("Zürich");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String city) {
                getWeatherData(city);
                searchView.setIconified(true);
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

        IntentFilter filter = new IntentFilter("Wetterupdate");
        registerReceiver(mBroadcastReceiver, filter);
        //sensorManager.registerListener(MainActivity.this, barometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE){
                    double airPressure = sensorEvent.values[0];
                    airPressureField.setText(airPressure + "pHa");
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
        unregisterReceiver(mBroadcastReceiver);
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
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherService weatherService = new WeatherService();
                WeatherData weatherData = weatherService.getWeatherData(cityNameWeather);

                if (weatherData.getTemp() > 25.0){
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator != null && vibrator.hasVibrator()){
                        VibrationEffect vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);
                        Log.i("VIBRATE", "Es vibriert");
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cityName.setText(cityNameWeather);
                        celsiusView.setText(weatherData.getTemp() + "° C");
                        weatherConditionDesc.setText(weatherData.getWeatherDesc());
                        Picasso.get().load(weatherData.getImg()).into(weatherConditionImg);
                    }
                });
            }
        });
        thread.start();
    }




}