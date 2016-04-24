package com.d3m0li5h3r.apps.weather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.d3m0li5h3r.apps.weather.apis.WeatherApi;
import com.d3m0li5h3r.apps.weather.models.Main;
import com.d3m0li5h3r.apps.weather.models.Sys;
import com.d3m0li5h3r.apps.weather.models.Weather;
import com.d3m0li5h3r.apps.weather.models.WeatherData;
import com.d3m0li5h3r.apps.weather.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Callback<WeatherData> {
    private final int ID_REQUEST_PERMISSIONS = 0x100;

    private TextView cityNameView;
    private TextView dateView;
    private TextView minMaxView;
    private TextView descriptionView;
    private TextView temperatureView;
    private TextView iconView;
    private FloatingActionButton fabView;

    private GoogleApiClient googleApiClient;

    private WeatherData weatherData;
    private WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabView = (FloatingActionButton) findViewById(R.id.fab);
        fabView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: inflate search bar with animation
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cityNameView = (TextView) findViewById(R.id.cityName);
        dateView = (TextView) findViewById(R.id.date);
        minMaxView = (TextView) findViewById(R.id.minMax);
        descriptionView = (TextView) findViewById(R.id.desc);
        temperatureView = (TextView) findViewById(R.id.temperature);
        iconView = (TextView) findViewById(R.id.icon);

        Typeface weatherFont = Typeface.createFromAsset(this.getAssets(), "weather.ttf");
        iconView.setTypeface(weatherFont);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, HH:mm");
        dateView.setText(dateFormat.format(Calendar.getInstance().getTime()));

        setProgressBarIndeterminateVisibility(true);

        checkPermissions();
        initRetrofit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (null != googleApiClient)
            googleApiClient.disconnect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case ID_REQUEST_PERMISSIONS:
                if (0 < grantResults.length
                        && PermissionChecker.PERMISSION_GRANTED == grantResults[0])
                    getLocation();
                else
                    Snackbar.make(fabView, R.string.permission_location_denied,
                            Snackbar.LENGTH_LONG)
                            .show();

                break;
            default:
                // Do nothing
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(fabView, R.string.permission_location_denied, Snackbar.LENGTH_LONG)
                    .show();

            return;
        }

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(100);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (null != location) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

            googleApiClient.disconnect();

            Call<WeatherData> call = weatherApi.getWeatherInformation(latitude, longitude);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
        setProgressBarIndeterminateVisibility(false);

        weatherData = response.body();

        if (404 == weatherData.getCode()) {
            Snackbar.make(fabView, weatherData.getMessage(), Snackbar.LENGTH_INDEFINITE).show();
            return;
        }

        updateUI();
    }

    @Override
    public void onFailure(Call<WeatherData> call, Throwable t) {
        setProgressBarIndeterminateVisibility(false);

        Snackbar.make(fabView, t.getLocalizedMessage(), Snackbar.LENGTH_INDEFINITE).show();
    }

    private void checkPermissions() {
        int permission = ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (PermissionChecker.PERMISSION_GRANTED != permission) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ID_REQUEST_PERMISSIONS);
            return;
        }

        getLocation();
    }

    private void getLocation() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();
    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.URL_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);
    }

    private void updateUI() {
        Sys sys = weatherData.getSys();
        Main main = weatherData.getMain();
        Weather weather = weatherData.getWeather()[0];

        StringBuilder cityNameBuilder = new StringBuilder();
        cityNameBuilder.append(weatherData.getName());
        if (!(null == sys || TextUtils.isEmpty(sys.getCountry())))
            cityNameBuilder.append(", ").append(sys.getCountry());
        cityNameView.setText(cityNameBuilder.toString());

        minMaxView.setText(String.format(getString(R.string.temperatureMinMax),
                Math.round(main.getTemp_max()), Math.round(main.getTemp_min())));

        descriptionView.setText(weather.getMain());

        temperatureView.setText(String.format(getString(R.string.temperature),
                Math.round(main.getTemp())));

        setIcon(weather.getId());
    }

    private void setIcon(int id) {
        int iconGroupId = id / 10;
        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        boolean isDayTime = 7 <= hourOfDay && 19 >= hourOfDay;

        if (800 == id) {
            if (isDayTime)
                //Day time
                iconView.setText(R.string.weather_clear_day);
            else
                iconView.setText(R.string.weather_clear_night);
        } else if (9 == id / 100) {
            switch (id) {
                case 900:
                    iconView.setText(R.string.weather_tornado);
                    break;
                case 901:
                    iconView.setText(R.string.weather_thunder_rain);
                    break;
                case 902:
                    iconView.setText(R.string.weather_hurricane);
                    break;
                case 903:
                    iconView.setText(R.string.weather_cold);
                    break;
                case 904:
                    iconView.setText(R.string.weather_hot);
                    break;
                case 905:
                    iconView.setText(R.string.weather_winds);
                    break;
                case 906:
                    iconView.setText(R.string.weather_hail);
                    break;
            }
        } else {
            switch (iconGroupId) {
                case 20:
                case 23:
                    iconView.setText(R.string.weather_thunder_rain);
                    break;
                case 21:
                case 22:
                    iconView.setText(R.string.weather_thunder_storm);
                    break;
                case 30:
                case 31:
                case 32:
                    iconView.setText(R.string.weather_drizzle);
                    break;
                case 50:
                    iconView.setText(R.string.weather_rain);
                    break;
                case 51:
                    iconView.setText(R.string.weather_rain_mix);
                case 52:
                case 53:
                    iconView.setText(R.string.weather_shower);
                case 60:
                case 62:
                    iconView.setText(R.string.weather_snow);
                    break;
                case 61:
                    iconView.setText(R.string.weather_sleet);
                    break;
                case 70:
                case 72:
                case 74:
                    iconView.setText(R.string.weather_mist);
                    break;
                case 71:
                    iconView.setText(R.string.weather_smoke);
                    break;
                case 73:
                case 75:
                case 76:
                    iconView.setText(R.string.weather_dust);
                    break;
                case 77:
                    iconView.setText(R.string.weather_winds);
                    break;
                case 78:
                    iconView.setText(R.string.weather_tornado);
                    break;
                case 80:
                    iconView.setText(R.string.weather_clouds);
                    break;
            }
        }
    }
}
