package com.cityfleet.util;

import com.cityfleet.model.MarkRoomAsRead;

/**
 * Created by vika on 21.04.16.
 */
public class MarkMessageSeenEvent {
    private MarkRoomAsRead markRoomAsRead;

    public MarkMessageSeenEvent(MarkRoomAsRead markRoomAsRead) {
        this.markRoomAsRead = markRoomAsRead;
    }

    public MarkRoomAsRead getMarkRoomAsRead() {
        return markRoomAsRead;
    }
}
