package com.example.jstore_android_lazuardinaufal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText emailInput = (EditText) findViewById(R.id.emailInput);
        final EditText passInput = (EditText) findViewById(R.id.passInput);
        final Button button = (Button) findViewById(R.id.loginButton);
        final TextView registerClickable = (TextView) findViewById(R.id.registerClickable);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String email = emailInput.getText().toString();
                final String password = passInput.getText().toString();
                Response.Listener<String> responseListener = new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse != null){
                                if (jsonResponse.getString("name") == "null") {
                                    throw new JSONException("name");
                                }
                                else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("currentUserId", jsonResponse.getInt("id"));
                                    intent.putExtra("currentUserName", jsonResponse.getString("name"));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                                builder1.setMessage("Login Success!").create().show();
                            }
                        } catch (JSONException e){
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
                            builder1.setMessage("Login Failed!").create().show();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });

        registerClickable.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent registIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registIntent);
            }
        });
    }
}
