package com.example.myapplication.activities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewService;
import com.example.myapplication.pojo.Record;

public class ItemRecordUi implements ViewService, View.OnClickListener {
    private Activity activity;
    private Record record;

    private TextView txt_title;
    private ImageView collapseImageView;
    private boolean isOpened;

    public ItemRecordUi(Activity activity, Record record) {
        this.activity = activity;
        this.record = record;
    }

    private int getViewId() {
        return R.layout.item_record;
    }

    @Override
    public View makeView(ViewGroup container) {
        return LayoutInflater.from(container.getContext()).inflate(this.getViewId(), container, false);
    }

    @Override
    public void onViewCreated(View view) {
        txt_title = view.findViewById(R.id.txt_title);
        txt_title.setText(getTitle());
        ((ImageView) view.findViewById(R.id.img_tick)).setImageDrawable(record.isCompleted() ? activity.getDrawable(R.drawable.ic_baseline_check_24) : null);
        collapseImageView = view.findViewById(R.id.img_collapse);
        collapseImageView.setImageDrawable(activity.getDrawable(isOpened ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_left_24));
        collapseImageView.setOnClickListener(this);
    }

    @Override
    public void initView() {
    }

    @Override
    public void onViewDestroyed() {
    }

    void toggle() {
        isOpened = !isOpened;
        collapseImageView.setImageDrawable(activity.getDrawable(isOpened ? R.drawable.ic_baseline_keyboard_arrow_down_24 : R.drawable.ic_baseline_keyboard_arrow_left_24));
        txt_title.setText(getTitle());
    }

    String getTitle() {
        String title = record.getTitle();
        if (title == null) {
            title = "N/A";
        } else if (!isOpened && title.length() > 20) {
            title = title.substring(0, 20) + "...";
        }
        return title;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_collapse:
                toggle();
                break;
        }
    }
}
