package ru.android_school.h_h.themostspb.Model;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BridgeAPI {

    @GET("bridges/bridges.json")
    Single<ArrayList<Bridge>> receiveBridges();

    @GET("bridges/{id}/bridge.json")
    Single<Bridge> getBridgeById(@Path("id") int id);
}
