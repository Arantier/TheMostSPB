package ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.BridgeSelector.MVPSelectorViewInterface;
import ru.android_school.h_h.themostspb.BridgeSelector.BridgeSelectorPresenter;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.BridgeSelector.Fragments.List.ListFragment;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.BridgeSelector.Fragments.MapFragment;
import ru.android_school.h_h.themostspb.InfoPage.InfoActivity.BridgeInfoActivity;

public class BridgeSelectorActivity extends AppCompatActivity implements ActivityCallback, MVPSelectorViewInterface {

    public static final int ERROR_MODE = -1,
            LOAD_MODE = 0,
            LIST_MODE = 1,
            MAP_MODE = 2;
    private static final String MODE_KEY = "mode_key";

    public static final String SHARED_PREFERENCES_TIMES = "timesandbridges";

    int mode;

    private BridgeSelectorPresenter presenter;

    private Toolbar toolbar;

    private ListFragment listFragment;
    private MapFragment mapFragment;

    private void setSwitchEnabled(boolean blockButton) {
        toolbar.getMenu().findItem(R.id.menu_toolbar_switch).setEnabled(blockButton);
    }

    private void setMode(int mode) {
        this.mode = mode;
        switch (mode) {
            case (LIST_MODE):
                findViewById(R.id.intermediateListState).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_fragment_container, listFragment)
                        .commit();
                toolbar.getMenu()
                        .findItem(R.id.menu_toolbar_switch)
                        .setIcon(R.drawable.selector_toolbar_map);
                break;
            case (MAP_MODE):
                findViewById(R.id.intermediateListState).setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_fragment_container, mapFragment)
                        .commit();
                toolbar.getMenu()
                        .findItem(R.id.menu_toolbar_switch)
                        .setIcon(R.drawable.selector_toolbar_list);
                break;
            case (LOAD_MODE): {
                ConstraintLayout layout = findViewById(R.id.intermediateListState);
                TextView description = layout.findViewById(R.id.stateDescription);
                description.setText(R.string.load_text);
                ImageView image = layout.findViewById(R.id.imageIndicator);
                image.setImageResource(R.drawable.ic_error_outline_24dp);
                layout.findViewById(R.id.refreshButton).setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
            break;
            case (ERROR_MODE): {
                ConstraintLayout layout = findViewById(R.id.intermediateListState);
                TextView description = layout.findViewById(R.id.stateDescription);
                description.setText(R.string.error_text);
                ImageView image = layout.findViewById(R.id.imageIndicator);
                image.setImageResource(R.drawable.ic_error_outline_red_24dp);
                layout.findViewById(R.id.refreshButton).setVisibility(View.VISIBLE);
                layout.setVisibility(View.VISIBLE);
            }
            break;
        }
    }

    private void setReadyState() {
        findViewById(R.id.intermediateListState).setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_selector);

        if (savedInstanceState != null) {
            mode = savedInstanceState.getInt(MODE_KEY);
        }

        presenter = new BridgeSelectorPresenter(this);
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

        //Установка Листенера на кнопку обновления ошибчного запроса
        findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMode(LOAD_MODE);
                presenter.requestAllBridges();
            }
        });
        if (listFragment == null) {
            setMode(LOAD_MODE);
            setSwitchEnabled(false);
            presenter.requestAllBridges();
        } else {
            setSwitchEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(MODE_KEY, mode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setData(Single<ArrayList<Bridge>> data) {
        data.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ArrayList<Bridge>>() {
                    @Override
                    public void accept(ArrayList<Bridge> bridges) {
                        listFragment = ListFragment.newInstance(bridges, BridgeSelectorActivity.this);
                        mapFragment = MapFragment.newInstance(bridges, BridgeSelectorActivity.this);
                        setReadyState();
                        setSwitchEnabled(true);
                        setMode(LIST_MODE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                        setSwitchEnabled(false);
                        setMode(ERROR_MODE);
                    }
                });
    }

    @Override
    public SharedPreferences getBridgeNotificationList() {
        return null;
    }

    @Override
    public void onBridgeClick(int id) {
        Intent bridgeInfoIntent = new Intent(this, BridgeInfoActivity.class);
        bridgeInfoIntent.putExtra(BridgeInfoActivity.ID_EXTRA, id);
        startActivity(bridgeInfoIntent);
    }

    @Override
    public boolean getNotificationState(int id) {
        SharedPreferences bridgesAndTimes = getSharedPreferences(SHARED_PREFERENCES_TIMES, Context.MODE_PRIVATE);;
        return bridgesAndTimes.contains(id+"");
    }
}
