package com.example.boss.lesson5.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.boss.lesson5.R;

/**
 * Created by BOSS on 27.10.2016.
 */

public class ViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;
    public ProgressBar progressBar;

    public ViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
    }
}
