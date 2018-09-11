package ru.android_school.h_h.themostspb.View.SelectorActivity;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
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

public class BridgeSelectorActivity extends AppCompatActivity implements OnBridgeActionListener, OnErrorRefreshListener {

    public static final int LIST_MODE = 1,
            MAP_MODE = 2,
            LOAD_MODE = 0;

    private int state = LOAD_MODE;

    private BridgePresenter presenter;

    private Toolbar toolbar;
    private FrameLayout fragmentContainer;

    int SWITCH_LIST = 1, SWITCH_MAP = 2;
    boolean switchButtonBlockade = true;
    int mode = SWITCH_LIST;

    private ListFragment listFragment;
    //    private MapFragment mapFragment;
    private LoadFragment loadFragment;
    private ErrorFragment errorFragment;

    private void switchButtonBlock(boolean blockButton) {
        toolbar.getMenu().findItem(R.id.menu_toolbar_switch).setEnabled(!blockButton);
    }

    private void setMode(int mode) {
        this.mode = mode;
        if (mode == SWITCH_LIST) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.layout_fragment_container, listFragment)
                    .commit();
        } else {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.layout_fragment_container,mapFragment)
//                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_selector);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFragment = LoadFragment.newInstance();
        errorFragment = ErrorFragment.newInstance(this);
        if (listFragment == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.layout_fragment_container, loadFragment)
                    .commit();
            switchButtonBlock(true);
            presenter.requestAllBridges();
        }
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
                        //mapFragment = MapFragment.newInstance(bridges,this);
                        switchButtonBlock(false);
                        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        if (mode == LIST_MODE) {
                            ft.replace(R.id.layout_fragment_container, listFragment);
                        } else {
//                    ft.replace(R.id.layout_fragment_container,mapFragment);
                        }
                        ft.commit();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                        switchButtonBlock(true);
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
        return (presenter.getNotificationDelay(id)>0);
    }

    @Override
    public void onErrorRefresh() {
        switchButtonBlock(true);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.layout_fragment_container, loadFragment)
                .commit();
        presenter.requestAllBridges();
    }
}
