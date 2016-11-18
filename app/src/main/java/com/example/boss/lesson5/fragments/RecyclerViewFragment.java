package com.example.boss.lesson5.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.activities.FullScreenActivity;
import com.example.boss.lesson5.adapters.RecyclerAdapter;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.providers.DataProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecyclerViewFragment extends Fragment implements View.OnTouchListener {


    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    public DiskLruImageCache diskCache;
    public RecyclerAdapter adapter;
    public EventBus bus;
    protected View mView;
    public RecyclerView recyclerView;
    public ImageButton buttonRow;
    public ImageButton buttonGrid;
    final public int bInKB = 1024;
    final private int startImageLoadRowThreshold = 3;
    final private int startImageLoadGridThreshold = 5;
    public static int lastPosition = 0;
    public static int layoutManagerType = 0;//0 = ROW Manager, 1 = GRID Manager


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);
        mView = view;
        Log.v(Constants.LOGS,"fragment onCreateView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        findAndSetViews();
        Log.v(Constants.LOGS,"fragment onStart");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(Constants.LOGS,"fragment onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(Constants.LOGS,"fragment onResume");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void findAndSetViews() {
        setUpEventBus();
        setUpCache();
        setUpButtons();
        adapter = new RecyclerAdapter(getActivity(), diskCache, DataProvider.getList());
        setUpRecycler();
    }

    public void setUpEventBus() {
        bus = EventBus.getDefault();
        if (!bus.isRegistered(this)) {
            bus.register(this);
        }
    }

    public void setUpCache() {
        final int cacheSize = bInKB * bInKB * 10;//10 MB
        diskCache = new DiskLruImageCache(getActivity(), Constants.CACHE_DIR, cacheSize);
    }

    public void setUpButtons() {
        buttonRow = (ImageButton) mView.findViewById(R.id.buttonRow);
        buttonGrid = (ImageButton) mView.findViewById(R.id.buttonGrid);
        setPressed();
        buttonRow.setOnTouchListener(this);
        buttonGrid.setOnTouchListener(this);
    }

    public void setPressed() {
        if (layoutManagerType == Constants.GRID_MANAGER) {
            buttonGrid.setPressed(true);
        } else {
            buttonRow.setPressed(true);
        }
    }

    public void setUpRecycler() {
        recyclerView = (RecyclerView) mView.findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        if (layoutManagerType == Constants.LINEAR_MANAGER) {
            setLinearManager();
        } else {
            setGridManager();
        }
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                RecyclerView.LayoutManager layMan = recyclerView.getLayoutManager();
                if (layMan != null) {
                    if (layoutManagerType == 0) {
                        lastPosition = ((LinearLayoutManager) layMan).findFirstVisibleItemPosition();
                        if (DataProvider.getList().size() - startImageLoadRowThreshold == ((LinearLayoutManager) layMan).findLastCompletelyVisibleItemPosition()) {
                            DataProvider.getUrls(getActivity(), DataProvider.query);
                        }
                    }
                    if (layoutManagerType == 1) {
                        lastPosition = ((GridLayoutManager) layMan).findFirstVisibleItemPosition();
                        if (DataProvider.getList().size() - startImageLoadGridThreshold == ((GridLayoutManager) layMan).findLastCompletelyVisibleItemPosition()) {
                            DataProvider.getUrls(getActivity(), DataProvider.query);
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.buttonRow:
                    if (!buttonRow.isPressed()) {
                        buttonRow.setPressed(true);
                        buttonGrid.setPressed(false);
                        setLinearManager();
                    }
                    break;
                case R.id.buttonGrid:
                    if (!buttonGrid.isPressed()) {
                        buttonGrid.setPressed(true);
                        buttonRow.setPressed(false);
                        setGridManager();
                    }
                    break;
            }
        }
        return true;
    }

    public void setGridManager() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        layoutManagerType = Constants.GRID_MANAGER;
        gridLayoutManager.scrollToPosition(lastPosition);
        bus.post((new CustomEvent()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
    }

    public void setLinearManager() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        layoutManagerType = Constants.LINEAR_MANAGER;
        linearLayoutManager.scrollToPosition(lastPosition);
        bus.post((new CustomEvent()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
    }


    @Subscribe
    public void onEvent(CustomEvent event) {
        switch (event.getEventMessage()) {
            case FULL_SCREEN:
                Intent intent = new Intent(getActivity(), FullScreenActivity.class);
                intent.putExtra(Constants.BUNDLE_KEY, event.getPosition());
                startActivity(intent);
                break;
            case NO_INTERNET:
                Toast.makeText(getActivity(), Constants.NO_INTERNET, Toast.LENGTH_SHORT).show();
                break;
        }
    }


}
