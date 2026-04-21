package com.shorif.e_commerce;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTP extends AppCompatActivity {
    EditText phone;
    Button submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        phone = findViewById(R.id.phone);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(view -> {
            String p = phone.getText().toString().trim();
            if (p.isEmpty()) {
                Toast.makeText(OTP.this, "Please enter phone number", Toast.LENGTH_SHORT).show();
                return;
            }
            String url = "https://dhakashoping.xyz/profile/otp.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getString("status").equals("success")) {
                                String whatsappLink = obj.getString("whatsapp_link");
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappLink));
                                startActivity(intent);
                            } else {
                                Toast.makeText(OTP.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(OTP.this, "Invalid response from server", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> Toast.makeText(OTP.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("phone", p); // send the actual phone
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(OTP.this);
            queue.add(request);
        });

    }
}