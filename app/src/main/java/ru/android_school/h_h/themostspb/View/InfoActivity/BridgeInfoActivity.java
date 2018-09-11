package ru.android_school.h_h.themostspb.View.InfoActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.BridgePresenter;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.View.BridgeView;

public class BridgeInfoActivity extends AppCompatActivity implements TimePickerDialog.OnNotificationStateChangeListener {

    public static final String ID_EXTRA = "id";

    private int id;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private BridgeView bridgeView;
    private TextView infoText;
    private View notificationButton;
    private View loadPlaceholder, errorPlaceholder;

    BridgePresenter presenter;

    @Override
    public void createNotificationAndRefreshButton(int minutesToCall) {
        presenter.setNotification(id,minutesToCall);
    }

    @Override
    public boolean getNotificationState() {
        return BridgeManager.getNotificationState(id);
    }

    @Override
    public void cancel() {

    }

    private class PagerAdapter extends FragmentPagerAdapter{

        String photoOpen;
        String photoClosed;


        public PagerAdapter(FragmentManager fm, String photoOpen, String photoClosed) {
            super(fm);
            this.photoOpen = photoOpen;
            this.photoClosed = photoClosed;
        }

        @Override
        public Fragment getItem(int i) {
            if (i == 0) {
                return ImageFragment.newInstance(photoOpen);
            } else {
                return ImageFragment.newInstance(photoClosed);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_info);
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        bridgeView = findViewById(R.id.bridgeView);
        infoText = findViewById(R.id.infoText);
        notificationButton = findViewById(R.id.notificationButton);
        loadPlaceholder = findViewById(R.id.loadPlaceholder);
        errorPlaceholder = findViewById(R.id.errorPlaceholder);
        errorPlaceholder.findViewById(R.id.button_errorRefresh)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshBridge();
                    }
                });
        presenter = BridgePresenter.getInstance();
        presenter.attachInfo(this);

        Intent intent = getIntent();
        id = intent.getIntExtra(ID_EXTRA,-1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        refreshBridge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachInfo();
    }

    private void refreshBridge(){
        presenter.requestBridge(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Bridge>() {
                    @Override
                    public void accept(Bridge bridge) throws Exception {
                        loadPlaceholder.setVisibility(View.GONE);
                        errorPlaceholder.setVisibility(View.GONE);
                        notificationButton.setVisibility(View.VISIBLE);

                        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(),bridge.bridgeConnectUrl,bridge.bridgeDivorseUrl);
                        viewPager.setAdapter(adapter);

                        bridgeView.setName(bridge.name);
                        bridgeView.setTimes(bridge.timeDivorse,bridge.timeConnect);
                        bridgeView.setDivorseState(BridgeManager.getDivorseState(bridge));
                        bridgeView.setNotificationState(BridgeManager.getNotificationState(bridge));
                        infoText.setText(Html.fromHtml(bridge.description));

                        TextView notificationButtonText = notificationButton.findViewById(R.id.notificationButtonText);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loadPlaceholder.setVisibility(View.GONE);
                        errorPlaceholder.setVisibility(View.VISIBLE);
                        notificationButton.setVisibility(View.GONE);
                    }
                });
    }

}
