package ru.android_school.h_h.themostspb.BridgeSelector;

import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;

import io.reactivex.Single;
import ru.android_school.h_h.themostspb.Model.Bridge;

public interface MVPSelectorViewInterface {

    void setData(Single<ArrayList<Bridge>> data);

    SharedPreferences getBridgeNotificationList();
}
