package com.networks.testapplication.utils;

import android.content.Context;

public class NetworkUtils {

    public NetworkUtils(){

    }

    public static NetworkUtils INSTANCE = new NetworkUtils();

    public boolean isNetworkConnected(Context context){
        return false;
    }
}
