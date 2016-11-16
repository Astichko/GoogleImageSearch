package com.example.boss.lesson5.providers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.boss.lesson5.Constants;

import java.util.ArrayList;

/**
 * Created by BOSS on 12.11.2016.
 */

public class DataProvider {
    private static ArrayList<ItemData> urlsList;
    public static boolean isSearching;//Determine while get url task is working.
    public static String query = "";
    public static String imageSize = Constants.MEDIUM;//medium by default

    public static ArrayList<ItemData> getList() {
        if (urlsList == null) {
            urlsList = new ArrayList<>();
        }
        return urlsList;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null && !activeNetwork.isConnected() && !activeNetwork.isAvailable()) {
            return false;
        }
        return true;
    }

}
