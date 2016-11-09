package com.example.boss.lesson5.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.boss.lesson5.adapters.RecyclerAdapter;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.holders.ViewHolder;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by BOSS on 28.10.2016.
 */

public class ImageNetLoadTask extends AsyncTask<String[], Void, Bitmap> {

    private DiskLruImageCache cache;
    private RecyclerAdapter adapter;
    //    private final WeakReference<ImageView> imageViewReference;
    private ImageView imageView;
    private ProgressBar progressBar;
    private int height;
    private int width;

    public ImageNetLoadTask(ViewHolder holder, DiskLruImageCache cache) {
        this.cache = cache;
        this.imageView = holder.imageView;
        this.progressBar = holder.progressBar;
        Log.v("myLogs", "onStartImageLoadTask");
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String[]... strings) {
        HttpURLConnection connection;
        FileOutputStream outputStream;
        try {
            Log.v("myLogs", "inProcessImageLoadTask");
            URL url = new URL(strings[0][0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            BitmapFactory.Options options = new BitmapFactory.Options();
            width = Integer.valueOf(strings[0][1]);
            height = Integer.valueOf(strings[0][2]);
            int size = calculateInSampleSize(500, 300);
            options.inSampleSize = size;
            options.inScaled = true;
            Log.v("myLogs", "inSampleSize: " + size);
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            //Save bitmap on disk
            cache.put(String.valueOf(strings[0][0].hashCode()), bitmap);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            if (imageView != null) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            }
        }
        Log.d("myLogs", "On post execute async task");
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
