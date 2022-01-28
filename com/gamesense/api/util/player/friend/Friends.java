



package com.gamesense.api.util.player.friend;

import java.util.*;

public class Friends
{
    public static List<Friend> friends;
    
    public Friends() {
        Friends.friends = new ArrayList<Friend>();
    }
    
    public static List<Friend> getFriends() {
        return Friends.friends;
    }
    
    public static List<String> getFriendsByName() {
        final ArrayList<String> friendsName = new ArrayList<String>();
        Friends.friends.forEach(friend -> friendsName.add(friend.getName()));
        return friendsName;
    }
    
    public static boolean isFriend(final String name) {
        boolean b = false;
        for (final Friend f : getFriends()) {
            if (f.getName().equalsIgnoreCase(name)) {
                b = true;
            }
        }
        return b;
    }
    
    public static Friend getFriendByName(final String name) {
        Friend fr = null;
        for (final Friend f : getFriends()) {
            if (f.getName().equalsIgnoreCase(name)) {
                fr = f;
            }
        }
        return fr;
    }
    
    public static void addFriend(final String name) {
        Friends.friends.add(new Friend(name));
    }
    
    public static void delFriend(final String name) {
        Friends.friends.remove(getFriendByName(name));
    }
}
