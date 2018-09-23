package ru.android_school.h_h.themostspb.View.SelectorActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.BridgePresenter;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.View.Fragments.ErrorFragment;
import ru.android_school.h_h.themostspb.View.Fragments.List.ListFragment;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.Fragments.LoadFragment;
import ru.android_school.h_h.themostspb.View.Fragments.MapFragment;

public class BridgeSelectorActivity extends AppCompatActivity implements ActivityCallback, OnErrorRefreshListener {

    public static final int LIST_MODE = 0,
            MAP_MODE = 1;
    private static final String MODE_KEY = "mode_key";
    private static final String PERMISSION_KEY = "perm_key";

    public static final int FINE_LOCATION = 2;

    int mode;

    private BridgePresenter presenter;

    private Toolbar toolbar;
    private FrameLayout fragmentContainer;


    private ListFragment listFragment;
    private MapFragment mapFragment;
    private LoadFragment loadFragment;
    private ErrorFragment errorFragment;

    private boolean locationPermission = false;

    private void setSwitchEnabled(boolean blockButton) {
        toolbar.getMenu().findItem(R.id.menu_toolbar_switch).setEnabled(blockButton);
    }

    private void setMode(int mode) {
        this.mode = mode;
        if (mode == LIST_MODE) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_fragment_container, listFragment)
                    .commit();
            toolbar.getMenu()
                    .findItem(R.id.menu_toolbar_switch)
                    .setIcon(R.drawable.ic_baseline_map_24_px);
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_fragment_container, mapFragment)
                    .commit();
            toolbar.getMenu()
                    .findItem(R.id.menu_toolbar_switch)
                    .setIcon(R.drawable.ic_list);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_selector);

        if (savedInstanceState!=null) {
            mode = savedInstanceState.getInt(MODE_KEY);
            locationPermission = savedInstanceState.getBoolean(PERMISSION_KEY);
        }

        //Не уверен правильно ли я тут поступил, но для преференсов нужен контекст
        presenter = BridgePresenter.getInstance();
        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_activity_selector);
        toolbar.getMenu()
                .findItem(R.id.menu_toolbar_switch)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (mode == LIST_MODE) {
                            setMode(MAP_MODE);
                        } else {
                            setMode(LIST_MODE);
                        }
                        return true;
                    }
                });
        fragmentContainer = findViewById(R.id.layout_fragment_container);
        presenter.attachSelector(this);

        loadFragment = LoadFragment.newInstance();
        errorFragment = ErrorFragment.newInstance(this);
        if (listFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_fragment_container, loadFragment)
                    .commit();
            setSwitchEnabled(false);
            presenter.requestAllBridges();
        } else {
            setSwitchEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        presenter.attachSelector(this);
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE_KEY,mode);
        outState.putBoolean(PERMISSION_KEY,locationPermission);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachSelector();
    }

    public void setData(Single<ArrayList<Bridge>> data) {
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<Bridge>>() {
                    @Override
                    public void accept(ArrayList<Bridge> bridges) throws Exception {
                        listFragment = ListFragment.newInstance(bridges, BridgeSelectorActivity.this);
                        mapFragment = MapFragment.newInstance(bridges, BridgeSelectorActivity.this);
                        setSwitchEnabled(true);
                        setMode(mode);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        setSwitchEnabled(false);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.layout_fragment_container, errorFragment)
                                .commit();
                    }
                });
    }

    @Override
    public void onBridgeClick(int id) {
        presenter.summonBridgeById(id);
    }

    @Override
    public boolean getNotificationState(int id) {
        return (presenter.getNotificationDelay(id) > 0);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    @Override
    public void requestUserLocation() {
        if (!locationPermission) {
            Log.d("Location", "Requesting location");
            boolean shouldProvideRationale =
                    ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.ACCESS_FINE_LOCATION);

            if (shouldProvideRationale) {
                showSnackbar(R.string.permission_rationale_message,
                        android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(BridgeSelectorActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        FINE_LOCATION);
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(BridgeSelectorActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION);
            }
        } else {

        }
    }

    @Override
    public void onErrorRefresh() {
        setSwitchEnabled(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment_container, loadFragment)
                .commit();
        presenter.requestAllBridges();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Location", "Request received");
        if (requestCode == FINE_LOCATION) {
            Log.d("Location", "Request asking for fine location");
            if (grantResults.length > 0 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Log.d("Location", "Permission granted");
                locationPermission = true;
                LocationProvider locationProvider = new LocationProvider(this);
                mapFragment.registerLocationProvider(locationProvider);
            } else {
                Log.d("Location", "Permission denied");
                locationPermission = false;
                mapFragment.registerLocationProvider(null);
            }
        }
    }
}
