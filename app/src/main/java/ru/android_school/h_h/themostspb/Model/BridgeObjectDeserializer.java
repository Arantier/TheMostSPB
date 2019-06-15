package ru.android_school.h_h.themostspb.Model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class BridgeObjectDeserializer implements JsonDeserializer<ArrayList<Bridge>> {
    @Override
    public ArrayList<Bridge> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json!=null) {
            JsonArray jsonBridgeArray = json.getAsJsonObject().get("objects").getAsJsonArray();
            ArrayList<Bridge> bridgeList = new ArrayList<>();
            for (JsonElement jsonBridge : jsonBridgeArray) {
                bridgeList.add((Bridge) context.deserialize(jsonBridge, Bridge.class));
            }
            return bridgeList;
        } else {
            throw new JsonParseException("Bad bridge parsing");
        }
    }
}
