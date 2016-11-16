package com.example.boss.lesson5.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.holders.ViewHolder;
import com.example.boss.lesson5.providers.ItemData;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BOSS on 28.10.2016.
 */

public class ImageNetLoadTask extends AsyncTask<ItemData, Void, Void> {

    private DiskLruImageCache cache;
    private ProgressBar progressBar;
    private ViewHolder holder;
    private int position;
    private int height;
    private int width;
    private EventMessage message;

    //RecyclerAdapter constructor
    public ImageNetLoadTask(ViewHolder holder, DiskLruImageCache cache) {
        this.cache = cache;
        this.holder = holder;
        this.progressBar = holder.progressBar;
        this.position = holder.getAdapterPosition();
        message = EventMessage.UPDATE_RECYCLER_ADAPTER;
        Log.v("myLogs", "onStartImageLoadTask");
    }

    //PageAdapter constructor
    public ImageNetLoadTask(ProgressBar progressBar, int position, DiskLruImageCache cache) {
        this.cache = cache;
        this.progressBar = progressBar;
        this.position = position;
        message = EventMessage.UPDATE_PAGE_ADAPTER;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Void doInBackground(ItemData... itemData) {
        itemData[0].conAttempts += 1;        //Increase number of connection attempts
        HttpURLConnection connection = null;
        InputStream input = null;
        try {
            itemData[0].isLoading = true;
            URL url = new URL(itemData[0].url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            BitmapFactory.Options options = new BitmapFactory.Options();
            width = Integer.valueOf(itemData[0].width);
            height = Integer.valueOf(itemData[0].height);
            options.inSampleSize = calculateInSampleSize(300, 200);
            options.inScaled = true;
            input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            //Save bitmap on disk
            cache.put(String.valueOf(url.hashCode()), bitmap);
            return null;
        } catch (Exception e) {
            itemData[0].isLoading = false;
            e.printStackTrace();
        } finally {
            itemData[0].isLoading = false;
            if (connection != null) {
                connection.disconnect();
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void avoid) {
        CustomEvent event = new CustomEvent();
        event.setPosition(position);
        EventBus.getDefault().post(event.setEventMessage(message));
    }

    public int calculateInSampleSize(int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}
