package com.example.jstore_android_lazuardinaufal;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelesaiPesananActivity extends AppCompatActivity
{
    int currentInvoiceId, currentUserId, itemPrice, totalPrice;
    String  currentUserName, invoiceDate,  invoiceType, invoiceStatus, itemName, duePeriod, staticDuePeriod;
    Button btnCancel, btnFinish;
    TextView tvCustomerName, tvItemName, tvItemPrice,
            tvInvoiceDate, tvInvoiceID, tvInvoiceType, tvInvoiceStatus,
            tvStaticDuePeriod, tvDuePeriod, tvTotalPrice, priceTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selesai_pesanan);
        tvCustomerName = findViewById(R.id.customer_name);
        tvStaticDuePeriod = findViewById(R.id.static_due_period);
        tvInvoiceID = findViewById(R.id.invoice_id);
        tvInvoiceStatus = findViewById(R.id.invoice_status);
        tvInvoiceType = findViewById(R.id.invoice_type);
        tvInvoiceDate = findViewById(R.id.invoice_date);
        tvDuePeriod = findViewById(R.id.due_period);
        tvItemName = findViewById(R.id.item_name);
        tvItemPrice = findViewById(R.id.selesai_item_price);
        tvTotalPrice = findViewById(R.id.total_price);
        btnCancel = findViewById(R.id.btnCancel);
        btnFinish = findViewById(R.id.btnFinish);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUserName = bundle.getString("currentUserName");
            currentUserId = bundle.getInt("currentUserId");
            itemName = bundle.getString("item_name");
            invoiceDate = bundle.getString("invoice_date");
            currentInvoiceId = bundle.getInt("id");
            totalPrice = bundle.getInt("total_price");
            invoiceStatus = bundle.getString("invoice_status");
            invoiceType = bundle.getString("invoice_type");
            itemPrice = bundle.getInt("item_price");
            staticDuePeriod = bundle.getString("st_due_period");
            duePeriod = bundle.getString("due_period");

        }
        tvItemName.setText(itemName);
        tvCustomerName.setText(currentUserName);
        tvInvoiceID.setText(currentInvoiceId+"");
        tvInvoiceDate.setText(invoiceDate);
        tvInvoiceType.setText(invoiceType);
        tvInvoiceStatus.setText(invoiceStatus);
        tvItemName.setText(itemName);
        tvItemPrice.setText("Rp " + itemPrice);
        tvTotalPrice.setText("Rp " + totalPrice);
        tvStaticDuePeriod.setText(staticDuePeriod);
        tvDuePeriod.setText(duePeriod);
        Log.d("currentInvoiceId", String.valueOf(currentInvoiceId));
        fetchPesanan();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject == null) {
                                Toast.makeText(SelesaiPesananActivity.this, "This invoice is canceled", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("currentUserId", currentUserId);
                                intent.putExtra("currentUserName", currentUserName);
                                startActivity(intent);
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Operation Failed! Please try again").create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SelesaiPesananActivity.this, "This invoice is canceled", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("currentUserId", currentUserId);
                            intent.putExtra("currentUserName", currentUserName);
                            startActivity(intent);
                        }
                    }
                };
                PesananBatalRequest request = new PesananBatalRequest(String.valueOf(currentInvoiceId), responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(request);
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                Toast.makeText(SelesaiPesananActivity.this, "This invoice is finished", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("currentUserId", currentUserId);
                                intent.putExtra("currentUserName", currentUserName);
                                startActivity(intent);
                            }
                            else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                                builder.setMessage("Operation Failed! Please try again").create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelesaiPesananActivity.this);
                            builder.setMessage("Operation Failed! Please try again").create().show();
                        }
                    }
                };
                PesananSelesaiRequest request = new PesananSelesaiRequest(String.valueOf(currentInvoiceId), responseListener);
                RequestQueue queue = Volley.newRequestQueue(SelesaiPesananActivity.this);
                queue.add(request);
            }
        });
    }

    public void fetchPesanan() {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.d("jsonObject", "onResponse: " + jsonObject);
                    if (jsonObject == null || jsonObject.getString("isActive").equals("false")) {
                        btnCancel.setEnabled(false);
                        btnFinish.setEnabled(false);
                    } else {
                        String date = jsonObject.getString("date");
                        JSONArray item_json = jsonObject.getJSONArray("item");
                        String invoiceType = jsonObject.getString("invoiceType");
                        String invoiceStatus = jsonObject.getString("invoiceStatus");
                        Integer totalPrice = jsonObject.getInt("totalPrice");
                        switch (invoiceStatus) {
                            case "Unpaid":
                                String dueDate = jsonObject.getString("dueDate");
                                break;
                            case "Installment":
                                String installmentPeriod = jsonObject.getString("installmentPeriod");
                                String installmentPrice = jsonObject.getString("installmentPrice");
                                break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        PesananFetchRequest request = new PesananFetchRequest(currentInvoiceId, responseListener);
        RequestQueue queue = new Volley().newRequestQueue(SelesaiPesananActivity.this);
        queue.add(request);
    }
}
