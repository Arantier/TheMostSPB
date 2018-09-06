package ru.android_school.h_h.themostspb.View;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import ru.android_school.h_h.themostspb.BridgePresenter;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.View.Fragments.ErrorFragment;
import ru.android_school.h_h.themostspb.View.Fragments.ListFragment;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.Fragments.LoadFragment;

public class BridgeSelectorActivity extends AppCompatActivity implements OnBridgeClickListener, OnErrorRefreshListener {

    public static final int LIST_MODE = 1,
            MAP_MODE = 2,
            LOAD_MODE = 0,
            ERROR_MODE = -1;

    private int state = LOAD_MODE;

    private BridgePresenter presenter;

    private Toolbar toolbar;
    private FrameLayout fragmentContainer;

    int SWITCH_LIST = 1, SWITCH_MAP = 2;
    boolean switchButtonBlockade = true;
    int switchButtonMode = SWITCH_LIST;

    private ListFragment listFragment;
    //    private MapFragment mapFragment;
    private LoadFragment loadFragment;
    private ErrorFragment errorFragment;

    private void switchButtonBlock(boolean blockButton) {

    }

    private void setSwitchButtonMode(int mode) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_selector);
        presenter = BridgePresenter.getInstance();
        toolbar = findViewById(R.id.toolbar);
        fragmentContainer = findViewById(R.id.layout_fragment_container);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment = LoadFragment.newInstance();
        //Надо определить колбек для обновления
        errorFragment = ErrorFragment.newInstance(this);
        presenter.attachSelector(this);
        presenter.requestData();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_fragment_container, loadFragment)
                .commit();
        switchButtonBlock(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.detachSelector();
    }

    public void setData(Single<ArrayList<Bridge>> data) {
        data.subscribe(new Consumer<ArrayList<Bridge>>() {
            @Override
            public void accept(ArrayList<Bridge> bridges) throws Exception {
                listFragment = ListFragment.newInstance(bridges, this);
                //mapFragment = MapFragment.newInstance(bridges,this);
                switchButtonBlock(false);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (switchButtonMode == LIST_MODE) {
                    ft.replace(R.id.layout_fragment_container, listFragment);
                } else {
//                    ft.replace(R.id.layout_fragment_container,mapFragment);
                }
                ft.commit();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.layout_fragment_container, errorFragment)
                        .commit();
            }
        });
    }

    private void setMode(int mode) {

    }

    @Override
    public void onBridgeClick(int id) {
        Intent intent = new Intent(this, BridgeInfoActivity.class);
        intent.putExtra(BridgeInfoActivity.ID_EXTRA, id);
        startActivity(intent);
    }

    @Override
    public void onErrorRefresh() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment_container,loadFragment)
                .commit();
        presenter.requestData();
    }
}
