package com.example.boss.lesson5.providers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.tasks.GetUrlsTask;

import org.greenrobot.eventbus.EventBus;

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
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }


    public static void getQuery(Context context, String query) {
        EventBus.getDefault().post((new CustomEvent()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
        DataProvider.getList().clear();
        getUrls(context, query);
    }

    public static void getUrls(Context context, String query1) {
        query = query1;
        if (isConnected(context)) {
            if (!isSearching) {
                new GetUrlsTask(query).execute();
            }
        } else {
            Toast.makeText(context, Constants.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }


}
