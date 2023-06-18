package com.raxim.myscoutee.data.mongo;

import java.util.List;

import com.raxim.myscoutee.profile.data.document.mongo.Event;
import com.raxim.myscoutee.profile.data.document.mongo.EventItem;

public class TestEvent extends Event {
    public void setInfo(TestEventItem info) {
        super.setInfo(info);
    }

    public void setItems(List<EventItem> items) {
        super.setItems(items);
    }
}
