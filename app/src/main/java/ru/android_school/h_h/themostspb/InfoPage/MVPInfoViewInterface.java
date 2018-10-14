package ru.android_school.h_h.themostspb.InfoPage;

import ru.android_school.h_h.themostspb.Model.Bridge;

public interface MVPInfoViewInterface {
    void launchNotification(Bridge bridge, int minutesToCall);

    void cancelNotification(int id);

    int getNotificationDelay(int id);
}
