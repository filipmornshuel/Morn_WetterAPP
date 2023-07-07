package ch.zli.mobile.morn_wetterapp;

import androidx.appcompat.app.AppCompatActivity;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {
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

        sensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_PRESSURE) {
                    double airPressure = sensorEvent.values[0];
                    airPressureField.setText(airPressure + " hPa");
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
    }

    private void getWeatherData(String cityNameWeather) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                WeatherService weatherService = new WeatherService();
                WeatherData weatherData = weatherService.getWeatherData(cityNameWeather);

                if (weatherData.getTemp() > 25.0) {
                    Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (vibrator.hasVibrator()) {
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("Wetterupdate".equals(intent.getAction())) {
                String city = intent.getStringExtra("city");
                double temperature = intent.getDoubleExtra("temperature", 0.0);
                String weatherDesc = intent.getStringExtra("weatherDesc");
                String iconId = intent.getStringExtra("iconId");

                cityName.setText(city);
                celsiusView.setText(temperature + " ° C");
                weatherConditionDesc.setText(weatherDesc);
                String iconUrl = "http://openweathermap.org/img/w/" + iconId + ".png";
                Picasso.get().load(iconUrl).into(weatherConditionImg);
            }
        }
    };
}