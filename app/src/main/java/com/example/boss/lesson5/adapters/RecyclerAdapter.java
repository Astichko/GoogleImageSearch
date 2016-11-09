package com.example.boss.lesson5.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boss.lesson5.R;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.holders.ViewHolder;
import com.example.boss.lesson5.tasks.ImageNetLoadTask;

import java.util.ArrayList;

/**
 * Created by BOSS on 27.10.2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private LayoutInflater inflater;
    public ArrayList<String[]> urlsList;
    private DiskLruImageCache cache;

    public RecyclerAdapter(Context context, DiskLruImageCache cache, ArrayList<String[]> urlsList) {
        inflater = LayoutInflater.from(context);
        this.urlsList = urlsList;
        this.cache = cache;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("myLogs", "onCreateViewHolder is called");

        return new ViewHolder(inflater.inflate(R.layout.card_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!urlsList.isEmpty()) {
            Log.v("myLogs", "here 1 ");
            String[] url = urlsList.get(position);
            Bitmap bitmap = cache.getBitmap(String.valueOf(url[0].hashCode()));
            if (bitmap == null) {
                holder.imageView.setImageBitmap(null);
            }
            if (cache != null && bitmap != null
                    && holder.imageView != null) {
                holder.progressBar.setVisibility(View.GONE);
                holder.imageView.setImageBitmap(bitmap);
            } else {
                ImageNetLoadTask loadImageTask = new ImageNetLoadTask(holder, cache);
                loadImageTask.execute(url);
            }
        }
    }

    @Override
    public int getItemCount() {
        return urlsList.size();
    }

//    public String hash(String string) {
//        MessageDigest messageDigest = null;
//        try {
//            messageDigest = MessageDigest.getInstance("SHA-256");
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            return null;
//        }
//        Log.d("myLogs",new String(messageDigest.digest()));
//        messageDigest.update(string.getBytes());
//        return new String(messageDigest.digest());
//    }

}
