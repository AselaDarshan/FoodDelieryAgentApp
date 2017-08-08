package com.siplo.fooddeliver;

import android.app.Application;

/**
 * Created by asela on 5/24/17.
 */
public class GlobalState extends Application {

    private static GlobalState singleton;
    private static boolean connected = false;
    public static int orderedItemCount=0;

    public static int getCurrrentUserId() {
        return currrentUserId;
    }

    public static void setCurrrentUserId(int currrentUserId) {
        GlobalState.currrentUserId = currrentUserId;
    }

    private static int currrentUserId;

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static void setCurrentUsername(String currentUsername) {
        GlobalState.currentUsername = currentUsername;
    }

    private static String currentUsername;

    public static boolean isConnected() {
        return connected;
    }

    public static void setConnected(boolean connected) {
        GlobalState.connected = connected;
    }




    public static GlobalState getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}