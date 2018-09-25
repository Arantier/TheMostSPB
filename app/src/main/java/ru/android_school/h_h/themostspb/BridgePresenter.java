package ru.android_school.h_h.themostspb;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;
import ru.android_school.h_h.themostspb.Model.BridgeProvider;
import ru.android_school.h_h.themostspb.View.InfoActivity.BridgeInfoActivity;
import ru.android_school.h_h.themostspb.View.NotificationReceiver;
import ru.android_school.h_h.themostspb.View.SelectorActivity.BridgeSelectorActivity;

public class BridgePresenter {

    private static BridgePresenter instance = null;

    public static final String SHARED_PREFERENCES_TIMES = "timesandbridges";

    //=============Сущности типа View===============
    private BridgeSelectorActivity selectorActivity;
    private BridgeInfoActivity infoActivity;

    //=============Сущности типа Model==============
    private BridgeProvider provider;

    public static BridgePresenter getInstance() {
        if (instance == null) {
            instance = new BridgePresenter();
        }
        return instance;
    }

    public void attachSelector(BridgeSelectorActivity selector) {
        if (selectorActivity != null) {
            detachSelector();
        }
        selectorActivity = selector;
    }

    public void detachSelector() {
        selectorActivity = null;
    }

    public void requestAllBridges() {
        Single<ArrayList<Bridge>> bridgeData = provider.provideBridges();
        selectorActivity.setData(bridgeData);
    }

    public void summonBridgeById(int id) {
        Intent summoning = new Intent(selectorActivity, BridgeInfoActivity.class);
        summoning.putExtra(BridgeInfoActivity.ID_EXTRA, id);
        selectorActivity.startActivity(summoning);
    }

    private BridgePresenter() {
        provider = new BridgeProvider("http://gdemost.handh.ru/api/v1/");
    }

    public void attachInfo(BridgeInfoActivity bridgeInfoActivity) {
        infoActivity = bridgeInfoActivity;
    }

    public void detachInfo() {
        infoActivity = null;
    }

    public Single<Bridge> requestBridge(int id) {
        return provider.getBridgeById(id);
    }

    public void createNotification(int id, final int minutesToCall) {
        requestBridge(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Bridge>() {
                    @Override
                    public void accept(Bridge bridge) {
                        launchNotification(bridge, minutesToCall);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    public void launchNotification(Bridge bridge, int minutesToCall){
        Intent launchIntent = new Intent(infoActivity, NotificationReceiver.class);
        launchIntent.setAction(NotificationReceiver.TAG);
        launchIntent.putExtra(NotificationReceiver.ID, bridge.id);
        launchIntent.putExtra(NotificationReceiver.NAME, bridge.name);
        launchIntent.putExtra(NotificationReceiver.MINUTES,minutesToCall);

        SharedPreferences bridgesAndTimes = infoActivity.getSharedPreferences(SHARED_PREFERENCES_TIMES,Context.MODE_PRIVATE);
        bridgesAndTimes.edit()
                .putInt(bridge.id+"",minutesToCall)
                .apply();

        Calendar momentToCall = BridgeManager.getClosestDivorse(bridge);
        momentToCall.add(Calendar.MINUTE,-minutesToCall);

        PendingIntent pIntent = PendingIntent.getBroadcast(infoActivity, bridge.id, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) infoActivity.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, momentToCall.getTimeInMillis(), pIntent);

        infoActivity.refreshStates();
    }

    public void cancelNotification(int id) {
        SharedPreferences bridgesAndTimes = infoActivity.getSharedPreferences(SHARED_PREFERENCES_TIMES,Context.MODE_PRIVATE);
        bridgesAndTimes.edit()
                .remove(id+"")
                .apply();

        Intent dummy = new Intent(infoActivity,NotificationReceiver.class);
        dummy.setAction(NotificationReceiver.TAG);
        PendingIntent stopIntent = PendingIntent.getBroadcast(infoActivity,id, dummy,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) infoActivity.getSystemService(Context.ALARM_SERVICE);
        am.cancel(stopIntent);
        stopIntent.cancel();

        infoActivity.refreshStates();
    }

    public int getNotificationDelay(int id) {
        SharedPreferences bridgesAndTimes = ((selectorActivity!=null) ? selectorActivity : infoActivity).getSharedPreferences(SHARED_PREFERENCES_TIMES,Context.MODE_PRIVATE);
        if (bridgesAndTimes.contains(id+"")){
            return bridgesAndTimes.getInt(id+"",0);
        } else {
            return 0;
        }
    }
}
