package com.example.myapplication.net.http;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VolleyRequest<T> extends Request<T> {
    private static RequestQueue requestQueue;

    private RequestCallback<T> callback;
    private RequestMethod requestMethod;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private byte[] requestBody;

    public VolleyRequest(Context context, RequestMethod method, String url, RequestCallback<T> callback) {
        super(method.VOLLEY_METHOD_CODE, url, callback);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
        this.requestMethod = method;
        this.url = url;
        if (callback == null) {
            throw new RuntimeException("callback cannot be null");
        }
        this.callback = callback;
    }

    @Override
    protected final Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T parsedResponse = callback.parseNetworkResponse(response);
            return Response.success(parsedResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Throwable ex) {
            return Response.error(new VolleyError(ex));
        }
    }

    @Override
    protected final void deliverResponse(T response) {
        callback.onResponse(response);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(KeyValuePair keyValuePair) {
        this.headers.put(keyValuePair.getKey(), keyValuePair.getValue());
    }

    @Override
    public final byte[] getBody() {
        return requestBody;
    }

    public void setRequestBody(byte[] requestBody) {
        this.requestBody = requestBody;
    }

    public RequestMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public String getUrl() {
        return url;
    }

    public void sendRequest() {
        requestQueue.add(this);
    }

    public static class KeyValuePair {
        private String key;
        private String value;

        public KeyValuePair(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public enum RequestMethod {
        GET("GET", Method.GET), POST("POST", Method.POST), PUT("PUT", Method.PUT),
        PATCH("PATCH", Method.PATCH), DELETE("DELETE", Method.DELETE);

        public final String METHOD;
        public final int VOLLEY_METHOD_CODE;

        RequestMethod(String method, int volleyMethodCode) {
            this.METHOD = method;
            this.VOLLEY_METHOD_CODE = volleyMethodCode;
        }
    }

    public interface RequestCallback<T> extends Response.Listener<T>, Response.ErrorListener {
        T parseNetworkResponse(NetworkResponse response);
    }
}
