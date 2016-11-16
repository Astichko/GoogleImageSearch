package com.example.boss.lesson5.activities;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.boss.lesson5.Constants;
import com.example.boss.lesson5.R;
import com.example.boss.lesson5.eventbus.CustomEvent;
import com.example.boss.lesson5.eventbus.EventMessage;
import com.example.boss.lesson5.providers.DataProvider;
import com.example.boss.lesson5.tasks.GetUrlsTask;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import static com.example.boss.lesson5.providers.DataProvider.isConnected;
import static com.example.boss.lesson5.providers.DataProvider.isSearching;
import static com.example.boss.lesson5.providers.DataProvider.query;


public class MainActivity extends AppCompatActivity {

    ActionBar actionBar;
    ProgressBar progressActionBar;
    ImageView searchIcon;
    ImageView delIcon;
    ImageView imageSize;

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
                getQuery();
                break;
            case Constants.MEDIUM:
                DataProvider.imageSize = Constants.MEDIUM;
                getQuery();
                break;
            case Constants.SMALL:
                DataProvider.imageSize = Constants.SMALL;
                getQuery();
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
                if (!query.equals(newQuery = editText.getText().toString())) {
                    query = newQuery;
                    //Clear old cache
                    getQuery();
                }
                return true;
            }
        });
    }

    public void getQuery() {
        EventBus.getDefault().post((new CustomEvent()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
        DataProvider.getList().clear();
        getUrls();
    }

    private void getUrls() {
        if (isConnected(this)) {
            if (!isSearching) {
                new GetUrlsTask(query).execute();
            }
        } else {
            Toast.makeText(this, Constants.NO_INTERNET, Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe
    public void onEvent(CustomEvent event) {
        switch (event.getEventMessage()) {
            case START_URL_SEARCH:
                searchIcon.setVisibility(View.GONE);
                progressActionBar.setVisibility(View.VISIBLE);
                isSearching = true;
                break;
            case FINISH_URL_SEARCH:
                progressActionBar.setVisibility(View.GONE);
                searchIcon.setVisibility(View.VISIBLE);
                EventBus.getDefault().post((new CustomEvent()).setEventMessage(EventMessage.UPDATE_RECYCLER_ADAPTER));
                isSearching = false;
                break;
        }
    }
}
