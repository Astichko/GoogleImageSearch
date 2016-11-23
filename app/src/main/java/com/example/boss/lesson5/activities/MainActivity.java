package com.example.boss.lesson5.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.adapters.RecyclerAdapter;
import com.example.boss.lesson5.eventbus.Event;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.providers.DataProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    ProgressBar progressActionBar;
    ImageView searchIcon;
    ImageView delIcon;
    ImageView imageSize;
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        setUpActionBar();
        setUpSearchBar();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.sizeIcon:
                menu.add(Constants.LARGE);
                menu.add(Constants.MEDIUM);
                menu.add(Constants.SMALL);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case Constants.LARGE:
                DataProvider.imageSize = Constants.LARGE;
                DataProvider.getQuery(this, query);
                break;
            case Constants.MEDIUM:
                DataProvider.imageSize = Constants.MEDIUM;
                DataProvider.getQuery(this, query);
                break;
            case Constants.SMALL:
                DataProvider.imageSize = Constants.SMALL;
                DataProvider.getQuery(this, query);
        }
        return super.onContextItemSelected(item);
    }

    public void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.search_view);
            progressActionBar = (ProgressBar) actionBar.getCustomView().findViewById(R.id.progressActionBar);
            searchIcon = (ImageView) actionBar.getCustomView().findViewById(R.id.searchIcon);
            delIcon = (ImageView) actionBar.getCustomView().findViewById(R.id.delIcon);
            imageSize = (ImageView) actionBar.getCustomView().findViewById(R.id.sizeIcon);
            imageSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    openContextMenu(imageSize);
                }
            });
            registerForContextMenu(imageSize);
        }
    }

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        EventBus.getDefault().post(new Event().setEventMessage(EventMessage.ON_CLOSE_CONTEXT_MENU));
    }

    public void setUpSearchBar() {
        final EditText editText = (EditText) actionBar.getCustomView().findViewById(R.id.edtSearch);
        editText.getBackground().mutate().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        delIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String newQuery;
                if (!(newQuery = editText.getText().toString()).equals("")) {
                    query = newQuery;
                    //Clear old cache
                    DataProvider.getQuery(editText.getContext(), query);
                }
                return true;
            }
        });
    }

    @Subscribe
    public void onEvent(Event event) {
        switch (event.getEventMessage()) {
            case START_URL_SEARCH:
                searchIcon.setVisibility(View.GONE);
                progressActionBar.setVisibility(View.VISIBLE);
                break;
            case FINISH_URL_SEARCH:
                progressActionBar.setVisibility(View.GONE);
                searchIcon.setVisibility(View.VISIBLE);
                EventBus.getDefault().post((new Event()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().unregister(RecyclerAdapter.class);
        super.onDestroy();
    }
}
