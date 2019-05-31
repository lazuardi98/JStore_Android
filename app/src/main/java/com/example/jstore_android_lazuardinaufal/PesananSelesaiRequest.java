package com.example.jstore_android_lazuardinaufal;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class PesananSelesaiRequest extends StringRequest
{
    private static final String URL = "http://10.0.2.2/finishtransaction";
    private Map<String, String> params;

    public PesananSelesaiRequest(String id, Response.Listener<String> listener)
    {
        super(Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("id_invoice", id);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
