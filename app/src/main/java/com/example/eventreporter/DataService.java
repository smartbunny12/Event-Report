package com.example.eventreporter;

import java.util.ArrayList;
import java.util.List;

public class DataService {
    /**
     * Fake all the event data for now, will refine this and connect to backend later.
     */
    public static List<Event> getEventData() {
        List<Event> eventData = new ArrayList<>();
        for (int i = 0; i< 10; i++) {
            eventData.add(
                    new Event("FLy Party" + i, "1184 W valley Blvd",
                            "Come to see lovely dogs!")
            );
        }
        return eventData;
    }
}
