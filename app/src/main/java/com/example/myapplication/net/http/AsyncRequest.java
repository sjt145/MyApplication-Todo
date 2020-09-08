package com.example.myapplication.net.http;

import android.content.Context;
import android.os.AsyncTask;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.example.myapplication.pojo.Record;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class AsyncRequest extends AsyncTask<Void, Void, Void> implements VolleyRequest.RequestCallback<AsyncRequest.Response> {
    private static final String URL = "https://jsonplaceholder.typicode.com/todos";
    private AsyncRequestListener callback;
    private VolleyRequest<Response> volleyRequest;

    public AsyncRequest(Context context, AsyncRequestListener callback) {
        this.callback = callback;
        volleyRequest = new VolleyRequest<>(context, VolleyRequest.RequestMethod.GET, URL, this);
    }

    @Override
    protected Void doInBackground(Void[] voids) {
        volleyRequest.sendRequest();
        return null;
    }

    @Override
    public final Response parseNetworkResponse(NetworkResponse networkResponse) {
        int statusCode = networkResponse.statusCode == 304 ? 200 : networkResponse.statusCode;
        if (statusCode != 200) {
            return new Response(statusCode, null);
        }
        JsonArray jsonArray = new JsonParser().parse(new String(networkResponse.data)).getAsJsonArray();
        ArrayList<Record> records = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            records.add(parseRecord(jsonElement.getAsJsonObject()));
        }
        return new Response(statusCode, records);
    }

    private Record parseRecord(JsonObject jsonObject) {
        return new Gson().fromJson(jsonObject, Record.class);
    }

    @Override
    public void onResponse(Response response) {
        callback.onResponseAvailable(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        onError(error);
    }

    public void onError(Exception ex) {
        callback.onResponseError(ex);
    }

    public static class Response {
        public final int code;
        public final ArrayList<Record> data;

        public Response(int code, ArrayList<Record> data) {
            this.code = code;
            this.data = data;
        }
    }

    public interface AsyncRequestListener {
        void onResponseAvailable(Response response);

        void onResponseError(Exception ex);
    }
}
