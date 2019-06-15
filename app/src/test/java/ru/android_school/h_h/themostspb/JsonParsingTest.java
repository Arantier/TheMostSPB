package ru.android_school.h_h.themostspb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeDeserializer;
import ru.android_school.h_h.themostspb.Model.BridgeObjectDeserializer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JsonParsingTest {

    private Bridge alexBridge;
    private Bridge rialtoBridge;
    private Bridge blessBridge;

    private ArrayList<Bridge> listOfBridge = new ArrayList<>();

    @Before
    public void prepareBridge() {
        alexBridge = new Bridge();
        alexBridge.id = 1;
        alexBridge.name = "Александра Невского";
        alexBridge.description = "Мост Алекса́ндра Не́вского — разводной мост через Неву в Санкт-Петербурге. Соединяет центральную часть города и правый берег Невы. На момент открытия это был не только самый крупный мост из предварительно напряженного железобетона в городе, но и один из крупнейших мостов такого типа в стране[1]. До 2004 г. (открытие Большого Обуховского (вантового) моста) являлся самым длинным мостом Санкт-Петербурга. Длина моста без береговых сооружений 629 м, вместе с пандусами — 905,7 м, ширина — 35 м.";
        alexBridge.bridgeDivorseUrl = "http://www.ipetersburg.ru/media/block_image_element/image/sizes/1104/12316/page_2e74ddc4ef8500149991ebee0b0eb621.jpg";
        alexBridge.bridgeConnectUrl = "http://visit-petersburg.ru/media/uploads/tourobject/196813/196813_cover.jpg.1050x700_q95_crop_upscale.jpg";
        alexBridge.longtitude = 30.3950853f;
        alexBridge.latitude = 59.9251358f;
        alexBridge.timeDivorse = new String[1];
        alexBridge.timeDivorse[0] = "02:20";
        alexBridge.timeConnect = new String[1];
        alexBridge.timeConnect[0] = "05:10";

        rialtoBridge = new Bridge();
        rialtoBridge.id = 2;
        rialtoBridge.name = "Биржевой";
        rialtoBridge.description = "Биржево́й мост (c 1922 по 1989 год — мост Строи́телей) — разводной мост через Малую Неву в Санкт-Петербурге. Соединяет между собой Васильевский и Петроградский острова.";
        rialtoBridge.bridgeDivorseUrl = "http://excava.ru/content_all/uploads/2016/02/birzhevoy-bridge.jpg";
        rialtoBridge.bridgeConnectUrl = "https://www.spb-guide.ru/img/468/12153.jpg";
        rialtoBridge.longtitude = 30.2929878f;
        rialtoBridge.latitude = 59.9463822f;
        rialtoBridge.timeDivorse = new String[1];
        rialtoBridge.timeDivorse[0] = "02:00";
        rialtoBridge.timeConnect = new String[1];
        rialtoBridge.timeConnect[0] = "04:55";

        blessBridge = new Bridge();
        blessBridge.id = 3;
        blessBridge.name = "Благовещенский";
        blessBridge.description = "Благове́щенский мост (ранее также Никола́евский мост и мост Лейтена́нта Шми́дта) — разводной мост через Неву в Санкт-Петербурге. Соединяет между собой Васильевский и 2-й Адмиралтейский острова. Первый постоянный мост через Неву. Ограждение, фонари и павильоны моста являются объектом культурного наследия.";
        blessBridge.bridgeDivorseUrl = "https://i.ptmap.ru/original/189.jpg";
        blessBridge.bridgeConnectUrl = "http://excava.ru/content_all/uploads/2016/02/blagoveshchenskiy-bridge.jpg";
        blessBridge.longtitude = 30.2910813f;
        blessBridge.latitude = 59.940625f;
        blessBridge.timeDivorse = new String[2];
        blessBridge.timeDivorse[0] = "01:25";
        blessBridge.timeDivorse[1] = "03:10";
        blessBridge.timeConnect = new String[2];
        blessBridge.timeConnect[0] = "02:45";
        blessBridge.timeConnect[1] = "05:00";

        listOfBridge.add(alexBridge);
        listOfBridge.add(rialtoBridge);
        listOfBridge.add(blessBridge);
    }

    @Test
    public void testBridgeJson(){
        String alexBridgeJson = "{\n" +
                "\"id\": 1,\n" +
                "\"name\": \"Александра Невского\",\n" +
                "\"description\": \"Мост Алекса́ндра Не́вского — разводной мост через Неву в Санкт-Петербурге. Соединяет центральную часть города и правый берег Невы. На момент открытия это был не только самый крупный мост из предварительно напряженного железобетона в городе, но и один из крупнейших мостов такого типа в стране[1]. До 2004 г. (открытие Большого Обуховского (вантового) моста) являлся самым длинным мостом Санкт-Петербурга. Длина моста без береговых сооружений 629 м, вместе с пандусами — 905,7 м, ширина — 35 м.\",\n" +
                "\"photo_open\": \"http://www.ipetersburg.ru/media/block_image_element/image/sizes/1104/12316/page_2e74ddc4ef8500149991ebee0b0eb621.jpg\",\n" +
                "\"photo_close\": \"http://visit-petersburg.ru/media/uploads/tourobject/196813/196813_cover.jpg.1050x700_q95_crop_upscale.jpg\",\n" +
                "\"lng\": 30.3950853,\n" +
                "\"lat\": 59.9251358,\n" +
                "\"divorces\": [\n" +
                "{\n" +
                "\"start\": \"02:20\",\n" +
                "\"end\": \"05:10\"\n" +
                "}\n" +
                "]\n" +
                "}";

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Bridge.class, new BridgeDeserializer())
                .create();
        Bridge alexBridgeParsed = gson.fromJson(alexBridgeJson,Bridge.class);
        assertEquals(alexBridge,alexBridgeParsed);
    }

    @Test
    public void testBridgeJsonArray() {
        String bridgeJsonString = "{\n" +
                "  \"objects\":[\n" +
                "    { \n" +
                "      \"id\": 1, \n" +
                "      \"name\": \"Александра Невского\", \n" +
                "      \"description\": \"Мост Алекса́ндра Не́вского — разводной мост через Неву в Санкт-Петербурге. Соединяет центральную часть города и правый берег Невы. На момент открытия это был не только самый крупный мост из предварительно напряженного железобетона в городе, но и один из крупнейших мостов такого типа в стране[1]. До 2004 г. (открытие Большого Обуховского (вантового) моста) являлся самым длинным мостом Санкт-Петербурга. Длина моста без береговых сооружений 629 м, вместе с пандусами — 905,7 м, ширина — 35 м.\", \n" +
                "      \"photo_open\": \"http://www.ipetersburg.ru/media/block_image_element/image/sizes/1104/12316/page_2e74ddc4ef8500149991ebee0b0eb621.jpg\", \n" +
                "      \"photo_close\": \"http://visit-petersburg.ru/media/uploads/tourobject/196813/196813_cover.jpg.1050x700_q95_crop_upscale.jpg\", \n" +
                "      \"lng\": 30.3950853, \n" +
                "      \"lat\": 59.9251358, \n" +
                "      \"divorces\":[ \n" +
                "        { \n" +
                "          \"start\": \"02:20\", \n" +
                "          \"end\": \"05:10\" \n" +
                "        } \n" +
                "      ] \n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": 2, \n" +
                "      \"name\": \"Биржевой\", \n" +
                "      \"description\": \"Биржево́й мост (c 1922 по 1989 год — мост Строи́телей) — разводной мост через Малую Неву в Санкт-Петербурге. Соединяет между собой Васильевский и Петроградский острова.\", \n" +
                "      \"photo_open\": \"http://excava.ru/content_all/uploads/2016/02/birzhevoy-bridge.jpg\", \n" +
                "      \"photo_close\": \"https://www.spb-guide.ru/img/468/12153.jpg\", \n" +
                "      \"lng\": 30.2929878, \n" +
                "      \"lat\": 59.9463822, \n" +
                "      \"divorces\":[ \n" +
                "        { \n" +
                "          \"start\": \"02:00\", \n" +
                "          \"end\": \"04:55\" \n" +
                "        } \n" +
                "      ] \n" +
                "    },\n" +
                "    { \n" +
                "      \"id\": 3, \n" +
                "      \"name\": \"Благовещенский\", \n" +
                "      \"description\": \"Благове́щенский мост (ранее также Никола́евский мост и мост Лейтена́нта Шми́дта) — разводной мост через Неву в Санкт-Петербурге. Соединяет между собой Васильевский и 2-й Адмиралтейский острова. Первый постоянный мост через Неву. Ограждение, фонари и павильоны моста являются объектом культурного наследия.\", \n" +
                "      \"photo_open\": \"https://i.ptmap.ru/original/189.jpg\", \n" +
                "      \"photo_close\": \"http://excava.ru/content_all/uploads/2016/02/blagoveshchenskiy-bridge.jpg\", \n" +
                "      \"lng\": 30.2910813, \n" +
                "      \"lat\": 59.940625, \n" +
                "      \"divorces\":[ \n" +
                "        { \n" +
                "          \"start\": \"01:25\", \n" +
                "          \"end\": \"02:45\" \n" +
                "        }, \n" +
                "        { \n" +
                "          \"start\": \"03:10\", \n" +
                "          \"end\": \"05:00\" \n" +
                "        } \n" +
                "      ] \n" +
                "    }\n" +
                "]}";
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ArrayList.class, new BridgeObjectDeserializer())
                .registerTypeAdapter(Bridge.class, new BridgeDeserializer())
                .create();
        ArrayList<Bridge> deserializedBridgeList = gson.fromJson(bridgeJsonString, ArrayList.class);

        assertArrayEquals(listOfBridge.toArray(),deserializedBridgeList.toArray());
    }
}
