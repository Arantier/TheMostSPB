package ru.android_school.h_h.themostspb.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class BridgeDeserializer implements JsonDeserializer<Bridge> {

    @Override
    public Bridge deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json!=null) {
            JsonObject jsonBridge = json.getAsJsonObject();
            Bridge bridge = new Bridge();
            JsonElement a = jsonBridge.get("id");
            bridge.id = jsonBridge.get("id").getAsInt();
            bridge.name = jsonBridge.get("name").getAsString();
            bridge.description = jsonBridge.get("description").getAsString();
            bridge.bridgeDivorseUrl = jsonBridge.get("photo_open").getAsString();
            bridge.bridgeConnectUrl = jsonBridge.get("photo_close").getAsString();
            bridge.longtitude = jsonBridge.get("lng").getAsFloat();
            bridge.latitude = jsonBridge.get("lat").getAsFloat();
            JsonArray jsonIntervals = jsonBridge.get("divorces").getAsJsonArray();
            bridge.timeDivorse = new String[jsonIntervals.size()];
            bridge.timeConnect = new String[jsonIntervals.size()];
            for (int i = 0; i < jsonIntervals.size(); i++) {
                JsonObject rawInterval = jsonIntervals.get(i).getAsJsonObject();
                bridge.timeDivorse[i] = rawInterval.get("start").getAsString().substring(0, 5);
                bridge.timeConnect[i] = rawInterval.get("end").getAsString().substring(0, 5);
            }
            return bridge;
        } else {
            throw new JsonParseException("Bad bridge parsing");
        }
    }
}
