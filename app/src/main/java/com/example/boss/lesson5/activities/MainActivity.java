package com.example.boss.lesson5.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.adapters.RecyclerAdapter;
import com.example.boss.lesson5.cache.DiskLruImageCache;
import com.example.boss.lesson5.tasks.GetUrlsTask;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    public DiskLruImageCache diskCache;
    public RecyclerAdapter adapter;
    ActionBar actionBar;
    ProgressBar progressActionBar;
    ImageView searchIcon;
    Button button1;
    Button button2;
    public static String query = "";
    ArrayList<String[]> urlsList = new ArrayList<>();
    final public int bInKB = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findAndSetViews();
        setUpActionBar();
        setUpSearchBar();
    }

    private void findAndSetViews() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        setUpCache();
        setUrlsList();
        adapter = new RecyclerAdapter(this, diskCache, urlsList);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button1.setPressed(true);
        button1.setOnTouchListener(this);
        button2.setOnTouchListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (urlsList.size() - 1 == layoutManager.findLastCompletelyVisibleItemPosition()) {
                    getUrls();
                }
            }
        });
    }

    public void setUpCache() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory() / bInKB;
        Log.v("myLogs", "Max memory is : " + maxMemory);
        final int cacheSize = 1024 * 1024 * 10;//10 MB

        diskCache = new DiskLruImageCache(this, Constants.CACHE_DIR, cacheSize);
    }

    public void setUrlsList() {
        urlsList = new ArrayList<>();
    }

    public void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.search_view);
            progressActionBar = (ProgressBar) actionBar.getCustomView().findViewById(R.id.progressActionBar);
            searchIcon = (ImageView) actionBar.getCustomView().findViewById(R.id.searchIcon);
        }
    }

    public void setUpSearchBar() {
        final EditText editText = (EditText) actionBar.getCustomView().findViewById(R.id.edtSearch);
        editText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String newQuery;
                if (!query.equals(newQuery = editText.getText().toString())) {
                    query = newQuery;
                    //Clear old cache
                    urlsList.clear();
                    adapter.notifyDataSetChanged();
                    getUrls();
                }
                return true;
            }
        });
    }

    private void getUrls() {
        new GetUrlsTask(urlsList, adapter, query, progressActionBar, searchIcon).execute();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            switch (view.getId()) {
                case R.id.button1:
                    if (view.isPressed()) {
                        view.setPressed(false);
                    } else {
                        view.setPressed(true);
                    }
                    break;
                case R.id.button2:
                    if (view.isPressed()) {
                        view.setPressed(false);
                    } else {
                        view.setPressed(true);
                    }
                    break;
            }
        }
        return true;
    }
}
