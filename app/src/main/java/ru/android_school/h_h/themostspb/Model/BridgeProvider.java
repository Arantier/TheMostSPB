package ru.android_school.h_h.themostspb.Model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class BridgeProvider {

    private String apiURL;
    private BridgeAPI api;

    private class DataDeserializer implements JsonDeserializer<ArrayList<Bridge>> {

        @Override
        public ArrayList<Bridge> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject rawData = json.getAsJsonObject();

            JsonArray rawArray = rawData.get("objects").getAsJsonArray();
            ArrayList<Bridge> finalArray = new ArrayList<>();

            for (JsonElement rawRawBridge : rawArray) {
                JsonObject rawBridge = rawRawBridge.getAsJsonObject();
                Bridge bridge = new Bridge();
                bridge.id = rawBridge.get("id").getAsInt();

                bridge.name = rawBridge.get("name").getAsString();
                bridge.description = rawBridge.get("description").getAsString();
                //TODO:Что за бред то, надо удостовериться в правильности фоток
                bridge.bridgeDivorseUrl = rawBridge.get("photo_open").getAsString();
                bridge.bridgeConnectUrl = rawBridge.get("photo_close").getAsString();

                bridge.longtitude = rawBridge.get("lng").getAsFloat();
                bridge.latitude = rawBridge.get("lat").getAsFloat();

                JsonArray rawIntervalsArray = rawBridge.get("divorces").getAsJsonArray();
                bridge.timeDivorse = new String[rawIntervalsArray.size()];
                bridge.timeConnect = new String[rawIntervalsArray.size()];
                for (int i = 0; i < rawIntervalsArray.size(); i++) {
                    JsonObject rawInterval = rawIntervalsArray.get(i).getAsJsonObject();
                    bridge.timeDivorse[i] = rawInterval.get("start").getAsString().substring(0, 5);
                    bridge.timeConnect[i] = rawInterval.get("end").getAsString().substring(0, 5);
                }

                finalArray.add(bridge);
            }

            return finalArray;
        }
    }

    public class BridgeDeserializer implements JsonDeserializer<Bridge> {

        @Override
        public Bridge deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Bridge bridge = new Bridge();
            JsonObject rawBridge = json.getAsJsonObject();

            bridge.id = rawBridge.get("id").getAsInt();

            bridge.name = rawBridge.get("name").getAsString();
            bridge.description = rawBridge.get("description").getAsString();
            bridge.bridgeDivorseUrl = rawBridge.get("photo_open").getAsString();
            bridge.bridgeConnectUrl = rawBridge.get("photo_close").getAsString();

            bridge.longtitude = rawBridge.get("lng").getAsFloat();
            bridge.latitude = rawBridge.get("lat").getAsFloat();

            JsonArray rawIntervalsArray = rawBridge.get("divorces").getAsJsonArray();
            bridge.timeDivorse = new String[rawIntervalsArray.size()];
            bridge.timeConnect = new String[rawIntervalsArray.size()];
            for (int i = 0; i < rawIntervalsArray.size(); i++) {
                JsonObject rawInterval = rawIntervalsArray.get(i).getAsJsonObject();
                bridge.timeDivorse[i] = rawInterval.get("start").getAsString().substring(0, 5);
                bridge.timeConnect[i] = rawInterval.get("end").getAsString().substring(0, 5);
            }

            return bridge;
        }
    }

    public BridgeProvider(String url) {
        apiURL = url;
        Retrofit.Builder builder = new Retrofit.Builder();
        Gson converter = new GsonBuilder()
                .registerTypeAdapter(ArrayList.class, new DataDeserializer())
                .registerTypeAdapter(Bridge.class, new BridgeDeserializer())
                .create();
        api = builder.baseUrl(apiURL)
                .addConverterFactory(GsonConverterFactory.create(converter))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(BridgeAPI.class);
    }

    public Single<ArrayList<Bridge>> provideBridges() {
        return api.receiveBridges();
    }

    public Single<Bridge> getBridgeById(int id) {
        return api.getBridgeById(id);
    }
}
