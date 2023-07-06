package ch.zli.mobile.morn_wetterapp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SearchView searchView;
    private TextView cityName;
    private TextView celsiusView;
    private ImageView weatherConditionImg;
    private TextView weatherConditionDesc;
    private TextView airPressureView;
    private TextView airPressureField;

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

    private void getWeatherData(String cityName){
        OkHttpClient client = new OkHttpClient();

        String apiKey = "4b90aa4b9b7faffcfae5b982f1ae1d45";

        String url = "https://api.openweathermap.org/data/2.5/weather?1="+cityName+"&appid="+apiKey;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()){
                String jsonData = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(jsonData);
                    double temperature = jsonObject.getJSONObject("main").getDouble("temp");
                    String weatherDesc = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");

                    WeatherData weatherData = new WeatherData();
                    weatherData.setTemp(temperature);
                    weatherData.setWeatherDesc(weatherDesc);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            } else {
                //Fehlerverarbeitung
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}