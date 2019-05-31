package com.example.jstore_android_lazuardinaufal;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/items";

    public MenuRequest(Response.Listener<String> listener)
    {
        super(Method.GET, URL, listener, null);
    }
}
