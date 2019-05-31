package com.example.jstore_android_lazuardinaufal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class BuatPesananActivity extends AppCompatActivity
{
    private int currentUserId;
    private int itemId;
    private String itemName;
    private String itemCategory;
    private String itemStatus;
    private double itemPrice;
    private int installmentPeriod;
    private String selectedPayment;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_pesanan);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            currentUserId = bundle.getInt("currentUserId");
            currentUserName = bundle.getString("currentUserName");
            itemId = bundle.getInt("item_id");
            itemName = bundle.getString("item_name");
            itemCategory = bundle.getString("item_category");
            itemStatus = bundle.getString("item_status");
            itemPrice = bundle.getInt("item_price");
        }
        final TextView item_name = findViewById(R.id.item_name);
        final TextView item_category = findViewById(R.id.item_category);
        final TextView item_status = findViewById(R.id.item_status);
        final TextView item_price = findViewById(R.id.item_price);
        final EditText installment_period = findViewById(R.id.installment_period);
        final TextView total_price = findViewById(R.id.total_price);
        final RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final Button hitung = findViewById(R.id.hitung);
        final Button pesan = findViewById(R.id.pesan);
        final TextView period = findViewById(R.id.textPeriod);
        hitung.setVisibility(View.VISIBLE);
        pesan.setVisibility(View.GONE);
        period.setVisibility(View.GONE);
        installment_period.setVisibility(View.GONE);
        item_name.setText(itemName);
        item_category.setText(itemCategory);
        item_status.setText(itemStatus);
        item_price.setText("Rp " + String.valueOf((int) itemPrice));
        total_price.setText("Rp 0");

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                String selectedButton = radioButton.getText().toString().trim();
                switch (selectedButton) {
                    case "Pay Now":
                        period.setVisibility(View.GONE);
                        installment_period.setVisibility(View.GONE);
                        break;
                    case "Pay Later":
                        period.setVisibility(View.GONE);
                        installment_period.setVisibility(View.GONE);
                        break;
                    case "Installment":
                        period.setVisibility(View.VISIBLE);
                        installment_period.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        hitung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadio = findViewById(selectedRadioId);
                String selectedButton = selectedRadio.getText().toString().trim();
                switch (selectedButton) {
                    case "Pay Now":
                        total_price.setText("Rp " + itemPrice);
                        break;
                    case "Pay Later":
                        total_price.setText("Rp " + itemPrice);
                        break;
                    case "Installment":
                        installmentPeriod = Integer.parseInt(installment_period.getText().toString());
                        total_price.setText("Rp. " + (itemPrice / installmentPeriod));
                        break;

                }
                hitung.setVisibility(view.GONE);
                pesan.setVisibility(view.VISIBLE);
            }
        });

        pesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedRadioId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadio = findViewById(selectedRadioId);
                String selected = selectedRadio.getText().toString().trim();
                String period = installment_period.getText().toString().trim();
                BuatPesananRequest request = null;
                final Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (response != null) {
                                Toast.makeText(BuatPesananActivity.this, "Order Success!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("currentUserId", currentUserId);
                                intent.putExtra("currentUserName", currentUserName);
                                startActivity(intent);
                            } else {
                                pesan.setVisibility(View.GONE);
                                Toast.makeText(BuatPesananActivity.this, "Order Failed!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("currentUserId", currentUserId);
                                intent.putExtra("currentUserName", currentUserName);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                String urlPaid = "http://10.0.2.2/createinvoicepaid";
                String urlUpaid = "http://10.0.2.2/createinvoiceunpaid";
                String urlInstallment = "http://10.0.2.2/createinvoiceinstallment";
                if(selected.equals("Pay Now")){
                    request = new BuatPesananRequest(itemId+"", currentUserId+"", urlPaid, responseListener);
                }
                else if(selected.equals("Pay Later")){
                    request = new BuatPesananRequest(itemId+"", currentUserId+""  , urlUpaid, responseListener);
                }
                else if(selected.equals("Installment")){
                    request = new BuatPesananRequest(itemId+"", period, currentUserId+""  , urlInstallment, responseListener);
                }
                RequestQueue queue = Volley.newRequestQueue(BuatPesananActivity.this);
                queue.add(request);
            }
        });
    }
}
