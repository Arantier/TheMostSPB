package ru.android_school.h_h.themostspb.Model;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class BridgeProvider {

    private String apiURL;

    public BridgeProvider(String url){
        apiURL = url;
    }

    public Single<ArrayList<Bridge>> provideBridges(){
        Retrofit.Builder builder = new Retrofit.Builder();
        Single<ArrayList<Bridge>> providedBridges = builder.baseUrl(apiURL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(/* TODO:Сделать */)
                .build()
                .create(BridgeAPI.class)
                .receiveBridges();
        return providedBridges;
    }
}
