package com.example.jstore_android_lazuardinaufal;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class PesananFetchRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/invoice/";

    public PesananFetchRequest(int id, Response.Listener<String> listener)
    {
        super(Method.GET, URL + id, listener, null);
        Log.d("","PesananFetchRequest: " + id);
    }
}
