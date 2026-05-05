package com.shorif.e_commerce;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotActivity extends AppCompatActivity {

    EditText email,otpCode;
    Button btnVerify,btnGetOtp;
    String otp="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        email = findViewById(R.id.email);
        otpCode =findViewById(R.id.otpCode);
        btnGetOtp=findViewById(R.id.btnGetOtp);
        btnVerify=findViewById(R.id.btnVerify);

        btnGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail = email.getText().toString().trim();
                if(mail.isEmpty()){
                    Toast.makeText(ForgotActivity.this, "Add an email", Toast.LENGTH_SHORT).show();
                    return;
                }

                String url="https://dhakashoping.xyz/otp.php";
                StringRequest stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        try {
                            JSONObject object=new JSONObject(s);
                            String status = object.getString("status");
                            String message = object.getString("message");
                            if(status.equals("success")){
                                Toast.makeText(ForgotActivity.this, message, Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ForgotActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(ForgotActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ForgotActivity.this, "error 5", Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String,String> map=new HashMap<>();
                        map.put("email",mail);
                        return map;
                    }
                };

                RequestQueue queue = Volley.newRequestQueue(ForgotActivity.this);
                queue.add(stringRequest);
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otpcode=otpCode.getText().toString().trim();
                if(!otp.isEmpty() && !otpcode.isEmpty()){
                    if(otp.equals(otpcode)){
                        Toast.makeText(ForgotActivity.this, "OTP Verified ✅", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotActivity.this, "Wrong OTP ❌", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }
}