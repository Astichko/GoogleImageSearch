package com.example.boss.lesson5.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.eventbus.Event;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.holders.ViewHolder;
import com.example.boss.lesson5.listeners.ImageClickListener;
import com.example.boss.lesson5.models.ItemData;
import com.example.boss.lesson5.providers.DataProvider;

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

    public RecyclerAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        EventBus.getDefault().register(this);
        this.context = context;
        this.urlsList = DataProvider.getList();
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
        ItemData item = urlsList.get(position);
        holder.imageView.setOnClickListener(new ImageClickListener(position, new ImageClickListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event event = new Event();
                event.setPosition(position);
                EventBus.getDefault().post(event.setEventMessage(EventMessage.FULL_SCREEN));
            }
        }));
        Glide.with(context).load(item.url)
                .asBitmap()
                .dontAnimate()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imageView);
    }

    @Subscribe
    public void onEvent(Event event) {
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
