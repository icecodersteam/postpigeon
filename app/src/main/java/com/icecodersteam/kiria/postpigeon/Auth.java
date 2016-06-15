package com.icecodersteam.kiria.postpigeon;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Auth extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        TextView reg = (TextView) findViewById(R.id.auth_gotoregistration);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Auth.this, RegistrationActivity.class);
                startActivity(intent);
                Auth.this.finish();
            }
        });
    }

    public void LoginButtonClick(View v){
        StringRequest strRequest = new StringRequest(Request.Method.POST, "http://postpigeon.handco.ru/pigeon.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jobj = new JSONObject(response);
                            if(jobj.has("access_token")){
                                SharedPreferences sPref = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor ed = sPref.edit();
                                ed.putString("token", jobj.getString("access_token"));
                                ed.commit();
                                Intent intent = new Intent(Auth.this, Dialogs.class);
                                startActivity(intent);
                                Auth.this.finish();
                            }else if(jobj.has("error_msg")){
                                Toast.makeText(Auth.this, jobj.getString("error_msg"), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e){
                            Toast.makeText(Auth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                EditText log = (EditText) findViewById(R.id.auth_login);
                EditText pas = (EditText) findViewById(R.id.auth_password);
                Map<String, String> params = new HashMap<String, String>();
                params.put("act", "auth");
                params.put("login", log.getText().toString());
                params.put("pass", pas.getText().toString());
                return params;
            }
        };
        QueueSingleton.getInstance(getApplicationContext()).addToRequestQueue(strRequest);
    }
}