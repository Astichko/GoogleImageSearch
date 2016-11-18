package com.example.boss.lesson5.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.providers.DataProvider;
import com.example.boss.lesson5.providers.ItemData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.boss.lesson5.Constants.ACCEPT;
import static com.example.boss.lesson5.Constants.APP_JSON;
import static com.example.boss.lesson5.Constants.CX;
import static com.example.boss.lesson5.Constants.FILE_TYPE;
import static com.example.boss.lesson5.Constants.GET;
import static com.example.boss.lesson5.Constants.HEIGHT;
import static com.example.boss.lesson5.Constants.ITEMS;
import static com.example.boss.lesson5.Constants.KEY;
import static com.example.boss.lesson5.Constants.LINK;
import static com.example.boss.lesson5.Constants.SEARCH_TYPE;
import static com.example.boss.lesson5.Constants.WIDTH;

/**
 * Created by BOSS on 03.11.2016.
 */

public class GetUrlsTask extends AsyncTask<Boolean, Void, Void> {

    private String query;
    private CustomEvent customEvent;

    public GetUrlsTask(String query) {
        this.query = query;
    }

    @Override
    protected void onPreExecute() {
        customEvent = new CustomEvent();
        customEvent.setEventMessage(EventMessage.START_URL_SEARCH);
        EventBus.getDefault().post(customEvent);
    }

    @Override
    protected Void doInBackground(Boolean... booleans) {
        DataProvider.isSearching = true;
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            ArrayList<ItemData> urlsList = DataProvider.getList();
            query = query.replaceAll("\\s+", "+");
            query = URLEncoder.encode(query, "UTF-8");
            String statItemIndex = String.valueOf(urlsList.size() + 1);
            URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + KEY +
                    "&cx=" + CX +
                    "&q=" + query +
                    "&fileType=" + FILE_TYPE +
                    "&searchType=" + SEARCH_TYPE +
                    "&alt=json" +
                    "&start=" + statItemIndex
                    + "&imgSize=" + DataProvider.imageSize);
            conn = (HttpURLConnection) url.openConnection();
            Log.v("myLogs", url.toString());
            conn.setRequestMethod(GET);
            conn.setRequestProperty(ACCEPT, APP_JSON);
            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            JSONObject json = new JSONObject(builder.toString());
            JSONArray items = json.getJSONArray(ITEMS);
            for (int i = 0; i < items.length(); i++) {
                String[] arr = {String.valueOf(items.getJSONObject(i).get(LINK)),
                        String.valueOf(items.getJSONObject(i).getJSONObject(SEARCH_TYPE).get(WIDTH)),
                        String.valueOf(items.getJSONObject(i).getJSONObject(SEARCH_TYPE).get(HEIGHT))};
                ItemData itemData = new ItemData();
                itemData.url = arr[0];
                itemData.width = arr[1];
                itemData.height = arr[2];

                urlsList.add(itemData);
            }
        } catch (Exception e) {
            EventBus.getDefault().post(EventMessage.NO_INTERNET);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        DataProvider.isSearching = false;
        customEvent.setEventMessage(EventMessage.FINISH_URL_SEARCH);
        EventBus.getDefault().post(customEvent);
    }
}
