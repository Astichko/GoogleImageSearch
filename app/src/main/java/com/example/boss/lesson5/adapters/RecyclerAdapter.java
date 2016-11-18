package com.example.boss.lesson5.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.holders.ViewHolder;
import com.example.boss.lesson5.listeners.ImageClickListener;
import com.example.boss.lesson5.providers.DataProvider;
import com.example.boss.lesson5.providers.ItemData;
import com.example.boss.lesson5.tasks.ImageNetLoadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

/**
 * Created by BOSS on 27.10.2016.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

    private final Context context;
    private LayoutInflater inflater;
    public ArrayList<ItemData> urlsList;
    private DiskLruImageCache cache;

    public RecyclerAdapter(Context context, DiskLruImageCache cache, ArrayList<ItemData> urlsList) {
        inflater = LayoutInflater.from(context);
        EventBus.getDefault().register(this);
        this.context = context;
        this.urlsList = urlsList;
        this.cache = cache;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.recycler_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        onImageBind(holder, position);
    }

    @Override
    public int getItemCount() {
        return urlsList.size();
    }

    public void onImageBind(final ViewHolder holder, int position) {
        if (!urlsList.isEmpty()) {
            ItemData item = urlsList.get(position);
            Bitmap bitmap = cache.getBitmap(String.valueOf(item.url.hashCode()));
            holder.noPageFound.setVisibility(View.GONE);
            if (bitmap == null) {
                holder.imageView.setImageBitmap(null);
            }
            if (cache != null && bitmap != null) {
                setBitmapFromCache(holder, position, bitmap);
            } else {
                netImageLoad(item, holder);
            }
        }
    }

    public void setBitmapFromCache(final ViewHolder holder, int position, Bitmap bitmap) {
        holder.progressBar.setVisibility(View.GONE);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(new ImageClickListener(position, new ImageClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CustomEvent customEvent = new CustomEvent();
                customEvent.setPosition(position);
                EventBus.getDefault().post(customEvent.setEventMessage(EventMessage.FULL_SCREEN));
            }
        }));
    }

    public void netImageLoad(ItemData item, ViewHolder holder) {
        if (!item.isLoading && DataProvider.isConnected(context)) {
            if (item.conAttempts <= Constants.MAX_CON_ATTEMPTS) {
                ImageNetLoadTask loadImageTask = new ImageNetLoadTask(holder, cache);
                loadImageTask.execute(item);
            } else {
                holder.progressBar.setVisibility(View.GONE);
                holder.noPageFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void onEvent(CustomEvent event) {
        switch (event.getEventMessage()) {
            case UPDATE_RECYCLER_ADAPTER:
                if (event.getPosition() != Constants.EVENT_WRONG_RESULT) {
                    this.notifyItemChanged(event.getPosition());
                } else {
                    this.notifyDataSetChanged();
                }
                break;
        }
    }
}
