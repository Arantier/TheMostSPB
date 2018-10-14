package ru.android_school.h_h.themostspb.InfoPage;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeProvider;

public class BridgeInfoPresenter {

    private MVPInfoViewInterface activity;
    private BridgeProvider provider;

    public BridgeInfoPresenter(MVPInfoViewInterface activity) {
        this.activity = activity;
        provider = new BridgeProvider("http://gdemost.handh.ru/api/v1/");
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
                        activity.launchNotification(bridge, minutesToCall);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }
}
