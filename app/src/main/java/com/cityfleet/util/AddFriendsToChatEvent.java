package com.cityfleet.util;

/**
 * Created by vika on 16.05.16.
 */
public class AddFriendsToChatEvent {
    private int[] allFriends;

    public AddFriendsToChatEvent(int[] allFriends) {
        this.allFriends = allFriends;
    }

    public int[] getAllFriends() {
        return allFriends;
    }
}
