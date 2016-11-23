package com.example.boss.lesson5.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.adapters.FullScreenPageAdapter;
import com.example.boss.lesson5.providers.DataProvider;

import org.greenrobot.eventbus.EventBus;

public class FullScreenActivity extends AppCompatActivity {

    private FullScreenPageAdapter adapter;
    private ViewPager viewPager;
    public ImageView arrowRight;
    public ImageView arrowLeft;
    public ImageView arrowBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        findViews();
        setAdapter();
    }

    public void findViews() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        arrowRight = (ImageView) findViewById(R.id.arrowRight);
        arrowLeft = (ImageView) findViewById(R.id.arrowLeft);
        arrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });
        arrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });
        arrowBack = (ImageView) findViewById(R.id.backArrow);
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getSupportActionBar().hide();
    }

    public void setAdapter() {
        adapter = new FullScreenPageAdapter(this, DataProvider.getList().size());
        viewPager.setAdapter(adapter);
        Bundle b = getIntent().getExtras();
        int position = 0;
        if (b != null) {
            position = b.getInt(Constants.BUNDLE_KEY);
        }
        viewPager.setCurrentItem(position);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(FullScreenPageAdapter.class);
        super.onDestroy();
    }
}
