package ru.android_school.h_h.themostspb.View.SelectorActivity;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationProvider implements LocationSource {

    /*
     *Задача: получить локацию и выкинуть её в карту
     */
    private static int DESIRED_REQUEST_INTERVAL = 10000;
    private static int FASTEST_REQUEST_INTERVAL = DESIRED_REQUEST_INTERVAL / 2;

    Context context;
    private FusedLocationProviderClient fusedClient;
    private SettingsClient settingsClient;

    Location currentLocation;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;

    public LocationProvider(Context context) {
        this.context = context;
        fusedClient = LocationServices.getFusedLocationProviderClient(context);
        settingsClient = LocationServices.getSettingsClient(context);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(DESIRED_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        //TODO:Ошибка тут
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        Log.d("Location","Location source activated");
        onLocationChangedListener.onLocationChanged(currentLocation);
    }

    @Override
    public void deactivate() {
        Log.d("Location","Location source deactivated");
    }

    public void startLocationUpdates(){
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        fusedClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                });
    }

    public void stopLocationUpdates(){
        fusedClient.removeLocationUpdates(locationCallback);
    }

}
