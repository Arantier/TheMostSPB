package ru.android_school.h_h.themostspb.Model;

import java.util.Calendar;

public class BridgeManager {

    public static final int BRIDGE_CONNECT = 0,
            BRIDGE_SOON = 1,
            BRIDGE_DIVORSE = 2;

    public static int getDivorseState(Bridge bridge) {
        Calendar now = Calendar.getInstance();
        Calendar closestDivorse = getClosestDivorse(bridge);
        Calendar closestConnection = getClosestConnection(bridge);
        if (closestConnection.compareTo(closestDivorse)<0){
            return BRIDGE_DIVORSE;
        } else if ((closestDivorse.getTimeInMillis()-now.getTimeInMillis())<3600000) {
            return BRIDGE_SOON;
        } else {
            return BRIDGE_CONNECT;
        }
    }

    public static Calendar getClosestDivorse(Bridge bridge){
        Calendar divorse = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        long diff=24*60*60*1000;
        for (String time : bridge.timeDivorse){
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            Calendar possibleDivorse = Calendar.getInstance();
            possibleDivorse.set(Calendar.HOUR_OF_DAY,hour);
            possibleDivorse.set(Calendar.MINUTE,minute);
            possibleDivorse.set(Calendar.SECOND,0);
            if (possibleDivorse.compareTo(now)<0){
                possibleDivorse.add(Calendar.DAY_OF_YEAR,1);
            }
            long possibleDiff = possibleDivorse.getTimeInMillis()-now.getTimeInMillis();
            if (possibleDiff<diff){
                diff = possibleDiff;
                divorse = possibleDivorse;
            }
        }
        return divorse;
    }

    public static Calendar getClosestConnection(Bridge bridge){
        Calendar connection = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        long diff=24*60*60*1000;
        for (String time : bridge.timeConnect){
            int hour = Integer.parseInt(time.split(":")[0]);
            int minute = Integer.parseInt(time.split(":")[1]);
            Calendar possibleConnection = Calendar.getInstance();
            possibleConnection.set(Calendar.HOUR_OF_DAY,hour);
            possibleConnection.set(Calendar.MINUTE,minute);
            possibleConnection.set(Calendar.SECOND,0);
            if (possibleConnection.compareTo(now)<0){
                possibleConnection.add(Calendar.DAY_OF_YEAR,1);
            }
            long possibleDiff = possibleConnection.getTimeInMillis()-now.getTimeInMillis();
            if (possibleDiff<diff){
                diff = possibleDiff;
                connection = possibleConnection;
            }
        }
        return connection;
    }
}
