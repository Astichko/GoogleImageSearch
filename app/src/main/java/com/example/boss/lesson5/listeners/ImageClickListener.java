package com.example.boss.lesson5.listeners;

import android.view.View;

/**
 * Created by BOSS on 13.11.2016.
 */

public class ImageClickListener implements View.OnClickListener {
    private ClickListener clickListener;
    private int position;

    public ImageClickListener(int position, final ClickListener clickListener) {
        this.clickListener = clickListener;
        this.position = position;
    }

    public interface ClickListener {
        void onClick(View view, int position);
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick(view, position);
    }
}
