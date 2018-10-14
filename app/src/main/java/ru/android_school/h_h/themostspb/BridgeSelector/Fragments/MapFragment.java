package ru.android_school.h_h.themostspb.BridgeSelector.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.BridgeView;
import ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity.ActivityCallback;

public class MapFragment extends SupportMapFragment implements LocationSource {

    private static final int REQUEST_CHECK_SETTINGS = 0x12;
    private static LatLng focusPoint = new LatLng(59.944196, 30.306196);

    ArrayList<Bridge> listOfBridges;
    ActivityCallback listener;

    public static final int FINE_LOCATION = 2;

    private static int DESIRED_REQUEST_INTERVAL = 10000;
    private static int FASTEST_REQUEST_INTERVAL = DESIRED_REQUEST_INTERVAL / 2;

    FrameLayout mainLayout;
    BridgeView bridgeView;

    boolean requestingLocationUpdates;

    private static final String KEY_REQUEST_LOCATION_UPDATES = "key_rlu";

    FusedLocationProviderClient fusedClient;
    SettingsClient settingsClient;
    LocationRequest locationRequest;
    LocationSettingsRequest locationSettingsRequest;
    LocationCallback locationCallback;
    OnLocationChangedListener onLocationChangedListener;

    GoogleMap map;
    Location currentLocation;

    public static MapFragment newInstance(ArrayList<Bridge> bridges, ActivityCallback listener) {
        MapFragment newInstance = new MapFragment();
        newInstance.listOfBridges = bridges;
        newInstance.listener = listener;
        return newInstance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mapFragment = super.onCreateView(inflater, container, savedInstanceState);
        mainLayout = new FrameLayout(getContext());
        mainLayout.addView(mapFragment);
        boolean locationPermission = (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);

        if (locationPermission) {
            initUserLocationServices();
            if (savedInstanceState != null) {
                requestingLocationUpdates = savedInstanceState.getBoolean(KEY_REQUEST_LOCATION_UPDATES);
            } else {
                requestingLocationUpdates = false;
            }
        }

        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.getUiSettings().setMapToolbarEnabled(false);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(focusPoint)
                        .zoom(10)
                        .bearing(0)
                        .tilt(0)
                        .build();
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                for (Bridge bridge : listOfBridges) {
                    LatLng bridgePosition = new LatLng(bridge.latitude, bridge.longtitude);
                    int iconResource;
                    Drawable markerDrawable;
                    switch (BridgeManager.getDivorseState(bridge)) {
                        case BridgeManager.BRIDGE_DIVORSE:
                            iconResource = R.drawable.ic_bridge_divorse;
                            break;
                        case BridgeManager.BRIDGE_SOON:
                            iconResource = R.drawable.ic_bridge_soon;
                            break;
                        default:
                            iconResource = R.drawable.ic_bridge_connect;
                            break;
                    }
                    markerDrawable = getResources().getDrawable(iconResource);
                    BitmapDescriptor bitmapDescriptor = getMarkerIconFromDrawable(markerDrawable);
                    MarkerOptions bridgeMarker = new MarkerOptions();
                    bridgeMarker.position(bridgePosition)
                            .icon(bitmapDescriptor)
                            .anchor(0.5f, 0.5f);
                    map.addMarker(bridgeMarker).setTag(bridge);
                }
                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Bridge currentBridge;
                        currentBridge = (Bridge) marker.getTag();
                        enableBridgeBar(currentBridge);
                        return false;
                    }
                });
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        disableBridgeBar();
                    }
                });
                if (fusedClient != null) {
                    startLocationUpdates();
                } else {
                    requestUserPermission();
                }
            }
        });
        return mainLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        if ((fusedClient != null) && (requestingLocationUpdates)) {
            startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putBoolean(KEY_REQUEST_LOCATION_UPDATES, requestingLocationUpdates);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void enableBridgeBar(final Bridge bridge) {
        if (bridgeView != null) {
            disableBridgeBar();
        }
        bridgeView = new BridgeView(getContext());
        bridgeView.setBackgroundColor(Color.WHITE);
        bridgeView.setName(bridge.name);
        bridgeView.setTimes(bridge.timeDivorse, bridge.timeConnect);
        bridgeView.setDivorseState(BridgeManager.getDivorseState(bridge));
        bridgeView.setNotificationState(listener.getNotificationState(bridge.id));
        bridgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onBridgeClick(bridge.id);
            }
        });
        FrameLayout.LayoutParams bridgeViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bridgeViewParams.gravity = Gravity.BOTTOM;
        mainLayout.addView(bridgeView, bridgeViewParams);
    }

    private void disableBridgeBar() {
        mainLayout.removeView(bridgeView);
        bridgeView = null;
    }

    private void initUserLocationServices() {
        fusedClient = LocationServices.getFusedLocationProviderClient(getActivity());
        settingsClient = LocationServices.getSettingsClient(getActivity());

        createLocationRequest();
        createLocationCallback();
        buildLocationSettingsRequest();
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(DESIRED_REQUEST_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_REQUEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("Location", "Asking for location");
                currentLocation = locationResult.getLastLocation();
                if (!map.isMyLocationEnabled()) {
                    map.setMyLocationEnabled(true);
                }
                onLocationChangedListener.onLocationChanged(currentLocation);
            }
        };
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        locationSettingsRequest = builder.build();
    }

    private void startLocationUpdates() {
        settingsClient.checkLocationSettings(locationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        fusedClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.myLooper());
                        Toast.makeText(getContext(), R.string.string_location_request, Toast.LENGTH_SHORT)
                                .show();
                        map.setLocationSource(MapFragment.this);
                        requestingLocationUpdates = true;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("Location", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    startIntentSenderForResult(rae.getResolution().getIntentSender(), REQUEST_CHECK_SETTINGS, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("Location", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("Location", errorMessage);
                                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                requestingLocationUpdates = false;
                        }

                    }
                });
    }

    private void stopLocationUpdates() {
        if (!requestingLocationUpdates) {
            return;
        }

        fusedClient.removeLocationUpdates(locationCallback)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        map.setMyLocationEnabled(false);
                        map.setLocationSource(null);
                        requestingLocationUpdates = false;
                    }
                });
    }

    private void requestUserPermission() {
        Log.d("Location", "Requesting location");
        boolean shouldProvideRationale = shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION);

        if (shouldProvideRationale) {
            AlertDialog rationaleDialog;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle(getString(R.string.permission_rationale_title))
                    .setMessage(getString(R.string.permission_rationale_message))
                    .setPositiveButton(getString(R.string.permission_rationale_close), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    FINE_LOCATION);
                        }
                    });
            rationaleDialog = dialogBuilder.create();
            rationaleDialog.show();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Location", "Request received");
        if (requestCode == FINE_LOCATION) {
            Log.d("Location", "Request asking for fine location");
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Location", "Permission granted");
                initUserLocationServices();
                startLocationUpdates();
            } else {
                Log.d("Location", "Permission denied");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.i("Location", "User agreed to make required location settings changes.");
                        requestingLocationUpdates = true;
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("Location", "User chose not to make required location settings changes.");
                        requestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        this.onLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        this.onLocationChangedListener = null;
    }
}
