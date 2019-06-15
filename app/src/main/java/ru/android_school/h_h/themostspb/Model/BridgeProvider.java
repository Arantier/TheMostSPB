package ru.android_school.h_h.themostspb.Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.android_school.h_h.themostspb.BridgeSelector.Fragments.List.ListFragment;
import ru.android_school.h_h.themostspb.BridgeSelector.Fragments.MapFragment;
import ru.android_school.h_h.themostspb.BridgeSelector.SelectorActivity.BridgeSelectorActivity;

public class BridgeProvider {

    private String apiURL;
    private BridgeAPI api;

    public BridgeProvider(String url) {
        apiURL = url;
        Retrofit.Builder builder = new Retrofit.Builder();
        Gson converter = new GsonBuilder()
                .registerTypeAdapter(ArrayList.class, new BridgeObjectDeserializer())
                .registerTypeAdapter(Bridge.class, new BridgeDeserializer())
                .create();
        api = builder.baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create(converter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BridgeAPI.class);
    }

    public Single<Bridge> getBridgeById(final int id) {
        return api.getBridgeById(id);
    }

    public Single<ArrayList<Bridge>> provideBridges() {
        return api.receiveBridges();
    }
}
