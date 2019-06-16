package ru.android_school.h_h.themostspb;

import android.support.test.runner.AndroidJUnit4;

import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.threeten.bp.LocalTime;
import org.threeten.bp.format.DateTimeFormatter;

import ru.android_school.h_h.themostspb.Model.Bridge;
import ru.android_school.h_h.themostspb.Model.BridgeManager;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class BridgeManagerTest {

    /*
        Классы, представляющие три типа мостов - разведённый, почти разведённый и сведённый
     */
    private static Bridge divorsedBridge;
    private static Bridge soonBridge;
    private static Bridge connectedBridge;
    //Внутренне представление формата записи времени вида "Часы:Минуты"
    private static DateTimeFormatter BRIDGE_TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /*
        Как и в прошлом примере, здесь инициализируются данные о мостах в минимальном формате -
        все поля пусты, кроме времён сведения/разведения.
     */
    @BeforeClass
    public static void prepareData() {
        divorsedBridge = new Bridge();
        divorsedBridge.timeDivorse = new String[1];
        divorsedBridge.timeConnect = new String[1];
        divorsedBridge.timeDivorse[0] = LocalTime.now().plusHours(-1).format(BRIDGE_TIME_FORMAT);
        divorsedBridge.timeConnect[0] = LocalTime.now().plusHours(1).format(BRIDGE_TIME_FORMAT);

        soonBridge = new Bridge();
        soonBridge.timeDivorse = new String[1];
        soonBridge.timeConnect = new String[1];
        soonBridge.timeDivorse[0] = LocalTime.now().plusMinutes(45).format(BRIDGE_TIME_FORMAT);
        soonBridge.timeConnect[0] = LocalTime.now().plusHours(1).format(BRIDGE_TIME_FORMAT);

        connectedBridge = new Bridge();
        connectedBridge.timeDivorse = new String[1];
        connectedBridge.timeConnect = new String[1];
        connectedBridge.timeDivorse[0] = LocalTime.now().plusHours(2).format(BRIDGE_TIME_FORMAT);
        connectedBridge.timeConnect[0] = LocalTime.now().plusHours(3).format(BRIDGE_TIME_FORMAT);
    }

    /*
        Следующие методы описывают сравнение кодов состояния, полученных методом
        BridgeManager.getDivorseState(), и ожидаемых кодов состояния
     */

    @Test
    public void testDivorseState() {
        int divorsedState = BridgeManager.getDivorseState(divorsedBridge);
        assertEquals(divorsedState, BridgeManager.BRIDGE_DIVORSE);
    }

    @Test
    public void testSoonState() {
        int soonState = BridgeManager.getDivorseState(soonBridge);
        assertEquals(soonState, BridgeManager.BRIDGE_SOON);
    }

    @Test
    public void divorseState() {
        int connectedState = BridgeManager.getDivorseState(connectedBridge);
        assertEquals(connectedState, BridgeManager.BRIDGE_CONNECT);
    }
}