package com.example.boss.lesson5.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.providers.ItemData;
import com.example.boss.lesson5.tasks.ImageNetLoadTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import static com.example.boss.lesson5.providers.DataProvider.getList;

/**
 * Created by BOSS on 14.11.2016.
 */

public class FullScreenPageAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private int size;
    private DiskLruImageCache diskCache;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView noPageFound;

    public FullScreenPageAdapter(Context context, DiskLruImageCache diskCache, int size) {
        this.context = context;
        this.diskCache = diskCache;
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
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        noPageFound = (TextView) view.findViewById(R.id.noPageFoundText);
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
        ArrayList<ItemData> list = getList();
        if (!list.isEmpty()) {
            ItemData item = list.get(position);
            Bitmap bitmap = diskCache.getBitmap(String.valueOf(item.url.hashCode()));
            if (bitmap == null) {
                imageView.setImageBitmap(null);
            }
            if (diskCache != null && bitmap != null) {
                progressBar.setVisibility(View.GONE);
                imageView.setImageBitmap(bitmap);
            } else {
                netImageLoad(item, position);
            }
        }
    }

    public void netImageLoad(ItemData item, int position) {
        if (!item.isLoading) {
            if (item.conAttempts < Constants.MAX_CON_ATTEMPTS) {
                ImageNetLoadTask loadImageTask = new ImageNetLoadTask(progressBar, position, diskCache);
                loadImageTask.execute(item);
            } else {
                noPageFound.setVisibility(View.VISIBLE);
            }
        }
    }

    @Subscribe
    public void onEvent(CustomEvent event) {
        switch (event.getEventMessage()) {
            case UPDATE_PAGE_ADAPTER:
                Log.v(Constants.LOGS, "PAGE ADAPTER UPDATED");
                this.notifyDataSetChanged();
                break;
        }
    }
}
