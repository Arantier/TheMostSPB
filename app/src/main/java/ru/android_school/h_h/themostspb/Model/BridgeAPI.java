package ru.android_school.h_h.themostspb.Model;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BridgeAPI {

    @GET("bridges/?format=json")
    Single<ArrayList<Bridge>> receiveBridges();

    @GET("bridges/{id}/?format=json")
    Single<Bridge> getBridgeById(@Path("id") int id);
}
