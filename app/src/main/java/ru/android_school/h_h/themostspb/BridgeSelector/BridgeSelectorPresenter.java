package ru.android_school.h_h.themostspb.BridgeSelector;

import android.content.SharedPreferences;

import java.util.ArrayList;

import io.reactivex.Single;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeProvider;
import ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity.BridgeSelectorActivity;

public class BridgeSelectorPresenter {

    //=============Сущности типа View===============
    private MVPSelectorViewInterface selectorView;

    //=============Сущности типа Model==============
    private BridgeProvider provider;

    public BridgeSelectorPresenter(BridgeSelectorActivity selectorActivity) {
        this.selectorView = selectorActivity;
        provider = new BridgeProvider("http://gdemost.handh.ru/api/v1/");
    }

    public void requestAllBridges() {
        Single<ArrayList<Bridge>> bridgeData = provider.provideBridges();
        selectorView.setData(bridgeData);
    }
}
