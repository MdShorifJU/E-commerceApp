package com.shorif.e_commerce;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class Login_Activity extends AppCompatActivity {

    TextInputEditText edittextPassword, edittextPhone;
    TextView Forgot_Text, Signup_Text;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        edittextPassword = findViewById(R.id.edittextPassword);
        edittextPhone = findViewById(R.id.edittextPhone);
        Forgot_Text = findViewById(R.id.Forgot_Text);
        Signup_Text = findViewById(R.id.Signup_Text);
        btnLogin = findViewById(R.id.btnLongin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass = edittextPassword.getText().toString().trim();
                String phone = edittextPhone.getText().toString().trim();

                if (pass.isEmpty() || phone.isEmpty()) {
                    if (pass.isEmpty()) edittextPassword.setError("Required");
                    if (phone.isEmpty()) edittextPhone.setError("Required");
                    return;
                }

                btnLogin.setEnabled(false);

                String url = "https://dhakashoping.xyz/profile/login.php";

                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                btnLogin.setEnabled(true);
                                if (s != null && s.trim().equalsIgnoreCase("Valid Login")) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("myApp", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("phone", phone);
                                    editor.putString("password",pass);
                                    editor.apply();
                                    Toast.makeText(Login_Activity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login_Activity.this, FirstShowProfile.class));
                                    finish();
                                } else if(s != null && s.trim().equalsIgnoreCase("Valid Login Admin")){
                                    SharedPreferences sharedPreferences = getSharedPreferences("myApp", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("phone", phone);
                                    editor.putString("password",pass);
                                    editor.apply();
                                    Toast.makeText(Login_Activity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Login_Activity.this, Admin_Activity.class));
                                    finish();
                                }else {
                                    showErrorDialog("⚠ Server Response");
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                btnLogin.setEnabled(true);
                                showErrorDialog(volleyError.getMessage());
                            }
                        }
                ) {
                    @NonNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        try {
                            if (MyMethods.my_key == null || MyMethods.my_key.isEmpty()) {
                                throw new AuthFailureError();
                            }
                            map.put("pass", MyMethods.encryptData(pass));
                            map.put("phone", MyMethods.encryptData(phone));
                            map.put("key", MyMethods.my_key);
                        } catch (Exception e) {
                            throw new AuthFailureError();
                        }
                        return map;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(Login_Activity.this);
                requestQueue.add(stringRequest);
            }
        });

        Signup_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Activity.this, SignUpActivity.class));
            }
        });

        Forgot_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login_Activity.this,OTP.class));
            }
        });
    }
    private void showErrorDialog(String title) {
        new AlertDialog.Builder(Login_Activity.this)
                .setTitle(title)
                .setMessage("Unable to connect\nTry again")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
