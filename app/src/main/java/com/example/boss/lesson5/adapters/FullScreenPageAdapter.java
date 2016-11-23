package com.example.boss.lesson5.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.eventbus.Event;
import com.example.boss.lesson5.models.ItemData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.example.boss.lesson5.providers.DataProvider.getList;

/**
 * Created by BOSS on 14.11.2016.
 */

public class FullScreenPageAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private int size;
    private ImageView imageView;

    public FullScreenPageAdapter(Context context, int size) {
        this.context = context;
        this.size = size;
        EventBus.getDefault().register(this);
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.page_adapter_item, container, false);
        imageView = (ImageView) view.findViewById(R.id.imageFSView);
        setImage(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void setImage(int position) {
        ItemData item = getList().get(position);
        Glide.with(context).load(item.url)
                .asBitmap()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getEventMessage()) {
            case UPDATE_PAGE_ADAPTER:
                this.notifyDataSetChanged();
                break;
        }
    }
}
