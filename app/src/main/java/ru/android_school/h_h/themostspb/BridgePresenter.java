package ru.android_school.h_h.themostspb;

import java.util.ArrayList;

import io.reactivex.Single;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeProvider;
import ru.android_school.h_h.themostspb.View.BridgeSelectorActivity;

public class BridgePresenter {

    private static BridgePresenter instance = null;

    //=============Сущности типа View===============
    private BridgeSelectorActivity selectorActivity;

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

    public void requestData(){
        Single<ArrayList<Bridge>> bridgeData = provider.provideBridges();
        selectorActivity.setData(bridgeData);
    }

    private BridgePresenter(){
        provider = new BridgeProvider("http://gdemost.handh.ru/api/v1/");
    }
}
