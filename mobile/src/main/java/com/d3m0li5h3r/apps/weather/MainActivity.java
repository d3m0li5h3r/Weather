package com.d3m0li5h3r.apps.weather;

import android.Manifest;
import android.content.pm.PackageManager;
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
import android.view.View;

import com.d3m0li5h3r.apps.weather.apis.WeatherApi;
import com.d3m0li5h3r.apps.weather.models.WeatherData;
import com.d3m0li5h3r.apps.weather.utils.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Callback<WeatherData> {
    private final int ID_REQUEST_PERMISSIONS = 10000;

    private FloatingActionButton fab;

    private GoogleApiClient googleApiClient;

    private WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: inflate search bar with animation
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        setProgressBarIndeterminateVisibility(true);

        //TODO: dump city names json to db

        checkPermissions();
        initRetrofit();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                    Snackbar.make(fab, R.string.permission_location_denied,
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
            Snackbar.make(fab, R.string.permission_location_denied, Snackbar.LENGTH_LONG)
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

            Snackbar.make(fab, "Lat:" + latitude + " Lon:" + longitude, Snackbar.LENGTH_SHORT)
                    .show();

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);

            googleApiClient.disconnect();

            Call<WeatherData> call = weatherApi.getWeatherInformation(latitude, longitude);
            call.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
        setProgressBarIndeterminateVisibility(false);

        WeatherData weatherData = response.body();
        Snackbar.make(fab, "Name:" + weatherData.getName(), Snackbar.LENGTH_INDEFINITE)
                .show();
    }

    @Override
    public void onFailure(Call<WeatherData> call, Throwable t) {
        setProgressBarIndeterminateVisibility(false);

        Snackbar.make(fab, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
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
}
