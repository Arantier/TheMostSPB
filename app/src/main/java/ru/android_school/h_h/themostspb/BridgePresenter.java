package ru.android_school.h_h.themostspb;

import android.content.Intent;

import java.util.ArrayList;

import io.reactivex.Single;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeProvider;
import ru.android_school.h_h.themostspb.View.BridgeInfoActivity;
import ru.android_school.h_h.themostspb.View.BridgeSelectorActivity;

public class BridgePresenter {

    private static BridgePresenter instance = null;

    //=============Сущности типа View===============
    private BridgeSelectorActivity selectorActivity;
    private BridgeInfoActivity infoActivity;

    //=============Сущности типа Model==============
    private BridgeProvider provider;

    public static BridgePresenter getInstance() {
        if (instance==null){
            instance = new BridgePresenter();
        }
        return instance;
    }

    public void attachSelector(BridgeSelectorActivity selector){
        if (selectorActivity!=null){
            detachSelector();
        }
        selectorActivity = selector;
    }

    public void detachSelector(){
        selectorActivity=null;
    }

    public void requestAllBridges(){
        Single<ArrayList<Bridge>> bridgeData = provider.provideBridges();
        selectorActivity.setData(bridgeData);
    }

    public void summonBridgeById(int id){
        Intent summoning  = new Intent(selectorActivity,BridgeInfoActivity.class);
        summoning.putExtra(BridgeInfoActivity.ID_EXTRA,id);
        selectorActivity.startActivity(summoning);
    }

    private BridgePresenter(){
        provider = new BridgeProvider("http://gdemost.handh.ru/api/v1/");
    }

    public void attachInfo(BridgeInfoActivity bridgeInfoActivity) {
        infoActivity = bridgeInfoActivity;
    }

    public void detachInfo(){
        infoActivity = null;
    }

    public Single<Bridge> requestBridge(int id) {
        return provider.getBridgeById(id);
    }
}
