package ru.android_school.h_h.themostspb.Model;

import java.util.Arrays;

public class Bridge {
    public int id;

    public String name;
    public String description;
    public String bridgeDivorseUrl;
    public String bridgeConnectUrl;

    public float longtitude;
    public float latitude;

    public String[] timeDivorse;
    public String[] timeConnect;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Bridge bridge = (Bridge) obj;
        return id == bridge.id
                && latitude == bridge.latitude
                && longtitude == bridge.longtitude
                && name.equals(bridge.name)
                && bridgeDivorseUrl.equals(bridge.bridgeDivorseUrl)
                && bridgeConnectUrl.equals(bridge.bridgeConnectUrl)
                && Arrays.equals(timeDivorse, bridge.timeDivorse)
                && Arrays.equals(timeConnect, bridge.timeConnect);
    }
    
    @Override
    public String toString() {
        String bridgeString = "Bridge: {"
                +"\n\tid: "+id
                +"\n\tname: "+name
                +"\n\tdescription: "+description
                +"\n\tphoto_open: "+bridgeDivorseUrl
                +"\n\tphoto_close: "+bridgeConnectUrl
                +"\n\tlongtitude: "+longtitude
                +"\n\tlatitude: "+latitude
                +"\n\tPeriods: {\n";
        for (int i=0;i<timeConnect.length;i++){
            bridgeString+="\t\t"+timeDivorse[i]+" - "+timeConnect[i]+"\n";
        }
        bridgeString+="\t}";
        bridgeString+= "\n}";
        return bridgeString;
    }
}
