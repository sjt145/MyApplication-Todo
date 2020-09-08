package com.example.myapplication.activities;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.RecyclerViewAdapter;
import com.example.myapplication.pojo.Record;

import java.util.ArrayList;

public class MainActivityUi implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private AppCompatActivity activity;
    private View loader;
    private RadioButton radio_all;
    private RadioButton radio_complete;
    private RadioButton radio_incomplete;
    private EditText txt_title_search;
    private RecyclerView recyclerView;

    private String text;
    private Boolean completedStatus;

    private RecyclerViewAdapter<ItemRecordUi> adapter = new RecyclerViewAdapter<>();
    private ArrayList<Record> records = new ArrayList<>();
    private ArrayList<Record> filteredRecords = new ArrayList<>();
    private int paginationIndex = 0;

    public MainActivityUi(AppCompatActivity activity) {
        this.activity = activity;
    }

    void initUi() {
        loader = activity.findViewById(R.id.loader);

        radio_all = activity.findViewById(R.id.radio_all);
        radio_complete = activity.findViewById(R.id.radio_complete);
        radio_incomplete = activity.findViewById(R.id.radio_incomplete);
        radio_all.setOnCheckedChangeListener(this);
        radio_complete.setOnCheckedChangeListener(this);
        radio_incomplete.setOnCheckedChangeListener(this);

        txt_title_search = activity.findViewById(R.id.txt_title_search);
        txt_title_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = s.toString();
                filterRecords();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        activity.findViewById(R.id.btn_prev).setOnClickListener(this);
        activity.findViewById(R.id.btn_next).setOnClickListener(this);

        recyclerView = activity.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewAdapter.RecyclerItemClickListener(activity) {
            @Override
            public void onItemClick(View touchedView, View clickedView, int index) {
                adapter.get(index).toggle();
                recyclerView.refreshDrawableState();
                recyclerView.requestLayout();
            }
        });
    }

    void setListData(ArrayList<Record> records) {
        this.records = records;
        filteredRecords = records;
        notifyListChanged();
    }

    void showLoader() {
        loader.setVisibility(View.VISIBLE);
    }

    void hideLoader() {
        loader.setVisibility(View.GONE);
    }

    void destroyUi() {
        loader = null;
        radio_all = null;
        radio_complete = null;
        radio_incomplete = null;
    }

    void filterRecords() {
        showLoader();
        paginationIndex = 0;
        ArrayList<Record> list = new ArrayList<>();
        for (Record record : records) {
            if ((text.equals("") || record.getTitle().contains(text))
                    && (completedStatus == null || record.isCompleted() == completedStatus)) {
                list.add(record);
            }
        }
        filteredRecords = list;
        notifyListChanged();
    }

    private void notifyListChanged() {
        showLoader();
        adapter.clear();
        if (!filteredRecords.isEmpty()) {
            int limit = Math.min(paginationIndex + 5, filteredRecords.size());
            for (int i = paginationIndex; i < limit; i++) {
                Record record = filteredRecords.get(i);
                adapter.add(new ItemRecordUi(activity, record));
            }
        }
        notifyAdapterChanged();
        hideLoader();
    }

    void onNextButtonClick() {
        int newPaginationIndex = paginationIndex + 5;
        if (newPaginationIndex >= filteredRecords.size()) {
            return;
        }
        showLoader();
        adapter.clear();
        int limit = Math.min(newPaginationIndex + 5, filteredRecords.size());
        for (int i = newPaginationIndex; i < limit; i++) {
            Record record = filteredRecords.get(i);
            adapter.add(new ItemRecordUi(activity, record));
        }
        paginationIndex = newPaginationIndex;
        notifyAdapterChanged();
    }

    void onPreviousButtonClick() {
        int newPaginationIndex = paginationIndex - 5;
        if (newPaginationIndex < 0) {
            return;
        }
        showLoader();
        adapter.clear();
        int limit = Math.min(newPaginationIndex + 5, filteredRecords.size());
        for (int i = newPaginationIndex; i < limit; i++) {
            Record record = filteredRecords.get(i);
            adapter.add(new ItemRecordUi(activity, record));
        }
        paginationIndex = newPaginationIndex;
        notifyAdapterChanged();
    }

    public void notifyAdapterChanged() {
        showLoader();
        adapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
        recyclerView.requestLayout();
        hideLoader();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_prev:
                onPreviousButtonClick();
                break;
            case R.id.btn_next:
                onNextButtonClick();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (!isChecked) {
            return;
        }
        text = txt_title_search.getText().toString();
        switch (compoundButton.getId()) {
            case R.id.radio_all:
                completedStatus = null;
                break;
            case R.id.radio_complete:
                completedStatus = true;
                break;
            case R.id.radio_incomplete:
                completedStatus = false;
                break;
        }
        filterRecords();
    }
}
