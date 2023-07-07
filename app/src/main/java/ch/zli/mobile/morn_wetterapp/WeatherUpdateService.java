package ch.zli.mobile.morn_wetterapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherUpdateService extends Service {
    private static final int NOTIFICATION_ID=1;
    private static final String CHANNEL_ID = "WeatherUpdateChannel";

    private Handler mHandler;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                String apiKey = "4b90aa4b9b7faffcfae5b982f1ae1d45";
                String countryCode = "CH";
                String language = "de";
                WeatherData weatherData = new WeatherData();
                String city = weatherData.getCityName();
                //Response response = client.newCall(request).execute();
                URL url1 = new URL("https://api.openweathermap.org/data/2.5/weather?q="+city+"&APPID="+apiKey+"&units=metric"+"&lang="+language);
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


                    weatherData.setTemp(temperature);
                    weatherData.setWeatherDesc(weatherDesc);
                    weatherData.setImg(iconId);

                    String iconUrl = "http://openweathermap.org/img/w/" + iconId +".png";

                    if(temperature > 25.0){
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (vibrator != null && vibrator.hasVibrator()){
                            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE);

                        }
                    }

                    Intent intent = new Intent("WetterUpdate");
                    intent.putExtra("city", city);
                    intent.putExtra("temperature", temperature);
                    intent.putExtra("weatherDesc", weatherDesc);
                    intent.putExtra("iconId", iconId);
                    sendBroadcast(intent);
                }
                connection.disconnect();
            }catch (IOException | JSONException e){
                e.printStackTrace();
            }

            mHandler.postDelayed(this, 60*100);
        }
    };

    /*


     */

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mHandler.post(mRunnable);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mRunnable);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}