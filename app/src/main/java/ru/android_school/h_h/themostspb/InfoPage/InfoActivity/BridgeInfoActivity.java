package ru.android_school.h_h.themostspb.InfoPage.InfoActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.InfoPage.BridgeInfoPresenter;
import ru.android_school.h_h.themostspb.InfoPage.MVPInfoViewInterface;
import ru.android_school.h_h.themostspb.InfoPage.NotificationReceiver;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.R;
import ru.android_school.h_h.themostspb.BridgeView;

import static ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity.BridgeSelectorActivity.SHARED_PREFERENCES_TIMES;

public class BridgeInfoActivity extends AppCompatActivity implements TimePickerDialog.OnNotificationStateChangeListener, MVPInfoViewInterface {

    public static final String ID_EXTRA = "id";

    public static final int ERROR_MODE = -1,
            LOAD_MODE = 0,
            READY_MODE = 1;

    private int id;
    private int mode;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private BridgeView bridgeView;
    private TextView infoText;
    private View notificationButton;

    BridgeInfoPresenter presenter;

    @Override
    public void createNotificationAndRefreshButton(int minutesToCall) {
        presenter.createNotification(id, minutesToCall);
    }

    @Override
    public boolean getNotificationState() {
        return (getNotificationDelay(id) > 0);
    }

    @Override
    public void cancel() {
        cancelNotification(id);
    }

    private class PagerAdapter extends FragmentPagerAdapter {

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
        findViewById(R.id.refreshButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refreshBridge();
                    }
                });
        presenter = new BridgeInfoPresenter(this);

        Intent intent = getIntent();
        id = intent.getIntExtra(ID_EXTRA, -1);
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
        setMode(LOAD_MODE);
        refreshBridge();
    }

    public void refreshStates() {
        //Для обновления развода слишком много текста писать, а выхлопа 0. Ну нафиг.
        bridgeView.setNotificationState(getNotificationDelay(id) > 0);

        TextView notificationButtonText = notificationButton.findViewById(R.id.notificationButtonText);
        String reminderText = "за ";
        int minutesToCall = getNotificationDelay(id);
        if ((minutesToCall > 0) && (minutesToCall < 60)) {
            reminderText += getResources().getQuantityString(R.plurals.minute_plurals, minutesToCall, minutesToCall);
        } else if (minutesToCall >= 60) {
            reminderText += getResources().getQuantityString(R.plurals.hours_plurals, minutesToCall / 60, minutesToCall / 60);
        } else {
            reminderText = getResources().getString(R.string.button_reminder);
        }
        reminderText = reminderText.toUpperCase();
        notificationButtonText.setText(reminderText);
    }

    private void refreshBridge() {
        presenter.requestBridge(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Bridge>() {
                    @Override
                    public void accept(final Bridge bridge) {
                        setMode(READY_MODE);
                        refreshStates();
                        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), bridge.bridgeConnectUrl, bridge.bridgeDivorseUrl);
                        viewPager.setAdapter(adapter);

                        bridgeView.setName(bridge.name);
                        bridgeView.setTimes(bridge.timeDivorse, bridge.timeConnect);
                        bridgeView.setDivorseState(BridgeManager.getDivorseState(bridge));
                        infoText.setText(Html.fromHtml(bridge.description));

                        {
                            notificationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    TimePickerDialog.newInstance(bridge.name, BridgeInfoActivity.this)
                                            .show(getSupportFragmentManager(), "timePickerDialog");
                                }
                            });
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        setMode(ERROR_MODE);
                    }
                });
    }

    @Override
    public void launchNotification(Bridge bridge, int minutesToCall) {
        Intent launchIntent = new Intent(this, NotificationReceiver.class);
        launchIntent.setAction(NotificationReceiver.TAG);
        launchIntent.putExtra(NotificationReceiver.ID, bridge.id);
        launchIntent.putExtra(NotificationReceiver.NAME, bridge.name);
        launchIntent.putExtra(NotificationReceiver.MINUTES, minutesToCall);

        SharedPreferences bridgesAndTimes = getSharedPreferences(SHARED_PREFERENCES_TIMES, Context.MODE_PRIVATE);
        bridgesAndTimes.edit()
                .putInt(bridge.id + "", minutesToCall)
                .apply();

        Calendar momentToCall = BridgeManager.getClosestDivorse(bridge);
        momentToCall.add(Calendar.MINUTE, -minutesToCall);

        PendingIntent pIntent = PendingIntent.getBroadcast(this, bridge.id, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, momentToCall.getTimeInMillis(), pIntent);

        refreshStates();
    }

    @Override
    public void cancelNotification(int id) {
        SharedPreferences bridgesAndTimes = getSharedPreferences(SHARED_PREFERENCES_TIMES, Context.MODE_PRIVATE);
        bridgesAndTimes.edit()
                .remove(id + "")
                .apply();

        Intent dummy = new Intent(this, NotificationReceiver.class);
        dummy.setAction(NotificationReceiver.TAG);
        PendingIntent stopIntent = PendingIntent.getBroadcast(this, id, dummy, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        am.cancel(stopIntent);
        stopIntent.cancel();

        refreshStates();
    }

    @Override
    public int getNotificationDelay(int id) {
        SharedPreferences bridgesAndTimes = getSharedPreferences(SHARED_PREFERENCES_TIMES,Context.MODE_PRIVATE);
        if (bridgesAndTimes.contains(id+"")){
            return bridgesAndTimes.getInt(id+"",0);
        } else {
            return 0;
        }
    }

    private void setMode(int mode) {
        this.mode = mode;
        switch (mode) {
            case (READY_MODE):
                notificationButton.setVisibility(View.VISIBLE);
                findViewById(R.id.intermediateListState).setVisibility(View.GONE);
                break;
            case (LOAD_MODE): {
                ConstraintLayout layout = findViewById(R.id.intermediateListState);
                TextView description = layout.findViewById(R.id.stateDescription);
                description.setText(R.string.load_text);
                ImageView image = layout.findViewById(R.id.imageIndicator);
                image.setImageResource(R.drawable.ic_error_outline_24dp);
                layout.findViewById(R.id.refreshButton).setVisibility(View.GONE);
                notificationButton.setVisibility(View.GONE);
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
                notificationButton.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
            }
            break;
        }
    }
}
