package com.example.myapplication.adapter;

import android.view.View;
import android.view.ViewGroup;

public interface ViewService {
    View makeView(ViewGroup viewGroup);

    void onViewCreated(View view);

    void initView();

    void onViewDestroyed();
}
