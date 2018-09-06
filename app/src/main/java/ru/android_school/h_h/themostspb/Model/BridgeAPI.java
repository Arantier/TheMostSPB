package ru.android_school.h_h.themostspb.Model;

import java.util.ArrayList;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface BridgeAPI {

    @GET("bridges/?format=json ")
    Single<ArrayList<Bridge>> receiveBridges();
}
