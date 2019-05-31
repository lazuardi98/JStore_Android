package com.example.jstore_android_lazuardinaufal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
{
    private ArrayList<Supplier> listSupplier = new ArrayList<>();
    private ArrayList<Item> listItem = new ArrayList<>();
    private HashMap<Supplier, ArrayList<Item>> childMapping = new HashMap<>();
    private int currentUserId;
    private String currentUserName;
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            currentUserId = bundle.getInt("currentUserId");
            currentUserName = bundle.getString("currentUserName");
        }
        final Button pesanan = findViewById(R.id.pesanan);
        expListView = (ExpandableListView) findViewById(R.id.mainExp);
        refreshList();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Item item = childMapping.get(listSupplier.get(groupPosition)).get(childPosition);
                int item_id = item.getId();
                int item_price = item.getPrice();
                String item_name = item.getName();
                String item_category = item.getCategory();
                String item_status = item.getStatus();
                Intent intent = new Intent(MainActivity.this, BuatPesananActivity.class);
                intent.putExtra("item_id",item_id);
                intent.putExtra("item_name",item_name);
                intent.putExtra("item_category",item_category);
                intent.putExtra("item_status",item_status);
                intent.putExtra("item_price",item_price);
                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("currentUserName", currentUserName);
                startActivity(intent);
                return true;
            }
        });

        pesanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelesaiPesananActivity.class);
                intent.putExtra("currentUserId", currentUserId);
                intent.putExtra("currentUserName", currentUserName);
                startActivity(intent);
            }
        });
    }

    protected void refreshList(){
        Response.Listener<String> responseListener = new Response.Listener<String>(){
            @Override
            public void onResponse(String response){
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    for (int i = 0; i < jsonResponse.length(); i++){
                        JSONObject item = jsonResponse.getJSONObject(i);
                        JSONObject supplier = item.getJSONObject("supplier");
                        JSONObject location = supplier.getJSONObject("location");
                        // location
                        String city = location.getString("city");
                        String province = location.getString("province");
                        String description = location.getString("description");
                        Location selectLocation = new Location(city, province, description);
                        // supplier
                        int supplierId = supplier.getInt("id");
                        String supplierName = supplier.getString("name");
                        String email = supplier.getString("email");
                        String phone = supplier.getString("phoneNumber");
                        Supplier selectSupplier = new Supplier(supplierId, supplierName, email, phone, selectLocation);
                        // item
                        int itemId = item.getInt("id");
                        int price = item.getInt("price");
                        String itemName = item.getString("name");
                        String category = item.getString("category");
                        String status = item.getString("status");
                        Item selectItem = new Item(itemId, itemName, status, price, selectSupplier, category);
                        // add to list
                        listItem.add(selectItem);
                        if (checkSupplier(selectSupplier.getId())){
                            listSupplier.add(selectSupplier);
                        }
                    }
                    // map to childMapping
                    for (int j = 0; j < listSupplier.size(); j++){
                        int id = listSupplier.get(j).getId();
                        ArrayList<Item> listItemBySupplier = new ArrayList<>(getItemFromSupplier(id));
                        childMapping.put(listSupplier.get(j), listItemBySupplier);
                        listItemBySupplier.clear();
                    }
                } catch (JSONException e){}
                listAdapter = new MainListAdapter(MainActivity.this, listSupplier, childMapping);
                expListView.setAdapter(listAdapter);
            }
        };
        MenuRequest menuRequest = new MenuRequest(responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(menuRequest);
    }

    protected ArrayList<Item> getItemFromSupplier(int id){
        ArrayList<Item> ItemBySupplier = new ArrayList<>();
        for (int i = 0; i < listItem.size(); i++){
            if (listItem.get(i).getSupplier().getId() == id){
                ItemBySupplier.add(listItem.get(i));
            }
        }
        if (ItemBySupplier.isEmpty()){
            return null;
        }
        else {
            return ItemBySupplier;
        }
    }

    protected boolean checkSupplier(int id){
        for (int i = 0; i < listSupplier.size(); i++){
            if (listSupplier.get(i).getId() == id){
                return false;
            }
        }
        return true;
    }
}
