package com.d3m0li5h3r.apps.weather.apis;

import com.d3m0li5h3r.apps.weather.models.WeatherData;
import com.d3m0li5h3r.apps.weather.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by d3m0li5h3r on 23/4/16.
 */
public interface WeatherApi {

    @GET("weather?units=metric&APPID=" + Constants.KEY_API)
    Call<WeatherData> getWeatherInformation(@Query("city") String city);

    @GET("weather?units=metric&APPID=" + Constants.KEY_API)
    Call<WeatherData> getWeatherInformation(@Query("lat") double lat, @Query("lon") double lon);
}
