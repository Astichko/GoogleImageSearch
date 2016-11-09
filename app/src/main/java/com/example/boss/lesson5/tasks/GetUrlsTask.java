package com.example.boss.lesson5.tasks;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.boss.lesson5.adapters.RecyclerAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by BOSS on 03.11.2016.
 */

public class GetUrlsTask extends AsyncTask<Void, Void, Void> {

    private ArrayList<String[]> urlsList;
    private RecyclerAdapter adapter;
    private String query;
    private ProgressBar progressActionBar;
    private ImageView searchIcon;

    public GetUrlsTask(ArrayList<String[]> urlsList, RecyclerAdapter adapter,
                       String query, ProgressBar progressActionBar, ImageView searchIcon) {
        this.urlsList = urlsList;
        this.adapter = adapter;
        this.query = query;
        this.progressActionBar = progressActionBar;
        this.searchIcon = searchIcon;
    }

    @Override
    protected void onPreExecute() {
        searchIcon.setVisibility(View.GONE);
        progressActionBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection conn;
        try {
            String key = "AIzaSyDrQ73hlYwaWHxnCN5jTwZgAPf5bugSpbM";//AIzaSyD1bRSmd04pI3zq3JGdsivxWFwTZPstGM4//AIzaSyDrQ73hlYwaWHxnCN5jTwZgAPf5bugSpbM
            String qry = query;
            qry = qry.replace(' ', '+');
            Log.d("myLogs","Query is: " + query);
            String cx = "011137781113453506451:y9on0vld-ri";//014336330805542817363:ynswprt0jd8//011137781113453506451:y9on0vld-ri
            String searchType = "image";
            String statItemIndex = String.valueOf(urlsList.size() + 1);
            URL url = new URL("https://www.googleapis.com/customsearch/v1?key="
                    + key + "&cx=" + cx + "&q=" + qry + "&searchType=" + searchType + "&alt=json&start=" + statItemIndex);
            conn = (HttpURLConnection) url.openConnection();
            Log.v("myLogs", url.toString());
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            JSONObject json = new JSONObject(builder.toString());
            JSONArray items = json.getJSONArray("items");
            for (int i = 0; i < items.length(); i++) {
                String[] arr = {String.valueOf(items.getJSONObject(i).get("link")),
                        String.valueOf(items.getJSONObject(i).getJSONObject("image").get("width")),
                               String.valueOf(items.getJSONObject(i).getJSONObject("image").get("height"))};
                urlsList.add(arr);
            }
            conn.disconnect();
            br.close();
        } catch (Exception e) {
            Log.d("myLogs", String.valueOf(e) + " GET URLS TASK PROBLEM");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        progressActionBar.setVisibility(View.GONE);
        searchIcon.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }
}
