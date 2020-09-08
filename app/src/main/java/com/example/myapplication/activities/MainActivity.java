package com.example.myapplication.activities;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.net.http.AsyncRequest;

public class MainActivity extends AppCompatActivity implements AsyncRequest.AsyncRequestListener {
    private MainActivityUi ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ui = new MainActivityUi(this);
        ui.initUi();
        loadData();
    }

    private void loadData() {
        try {
            if (!isNetworkAvailable()) {
                showToast(getString(R.string.error_no_network));
                return;
            }
            ui.showLoader();
            new AsyncRequest(this, this).execute();
        } catch (Exception ex) {
            ex.printStackTrace();
            ui.hideLoader();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (ui != null) {
                ui.destroyUi();
                ui = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onResponseAvailable(AsyncRequest.Response response) {
        try {
            ui.setListData(response.data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ui.hideLoader();
    }

    @Override
    public void onResponseError(Exception ex) {
        ui.hideLoader();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}