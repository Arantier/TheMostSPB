package ru.android_school.h_h.themostspb.View.Fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.BridgeView;
import ru.android_school.h_h.themostspb.View.SelectorActivity.ActivityCallback;
import ru.android_school.h_h.themostspb.View.SelectorActivity.LocationProvider;

public class MapFragment extends SupportMapFragment {

    private static LatLng focusPoint = new LatLng(59.944196, 30.306196);

    ArrayList<Bridge> listOfBridges;
    ActivityCallback listener;

    FrameLayout mainLayout;
    BridgeView bridgeView;

    LocationProvider locationProvider;

    GoogleMap map;

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
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                map.setMinZoomPreference(10);
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
                listener.requestUserLocation();
            }
        });
        return mainLayout;
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

    public void registerLocationProvider(LocationProvider newLocationProvider) {
        locationProvider = newLocationProvider;
//        map.setLocationSource(locationProvider);
//        if (locationProvider != null) {
//            locationProvider.startLocationUpdates();
//        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (locationProvider != null) {
            locationProvider.startLocationUpdates();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationProvider != null) {
            locationProvider.stopLocationUpdates();
        }
    }
}
