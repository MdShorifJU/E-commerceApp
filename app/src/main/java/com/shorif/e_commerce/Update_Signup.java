package com.shorif.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Update_Signup extends AppCompatActivity {

    EditText usernameEdit,emailEdit;
    Button submitBtn;
    SharedPreferences sharedPreferences;
    TextView alreadyLogin;
    ImageView backImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_signup);

        usernameEdit = findViewById(R.id.usernameEdit);
        emailEdit = findViewById(R.id.emailEdit);
        submitBtn = findViewById(R.id.submitBtn);
        alreadyLogin = findViewById(R.id.alreadyLogin);
        backImg=findViewById(R.id.backImg);
        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = usernameEdit.getText().toString().trim();
                String email = emailEdit.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty()) {

                    String url = "https://dhakashoping.xyz/update_signup.php";

                    StringRequest request = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        String status = jsonObject.getString("status");
                                        String message = jsonObject.getString("message");

                                        if (status.equals("success")) {

                                            SharedPreferences.Editor editor = sharedPreferences.edit();

                                            String tableName = "user_" + name.replace(" ", "_");
                                            editor.putString("userName", name);
                                            editor.putString("email", email);
                                            editor.putString("table",tableName);
                                            editor.apply();

                                            Toast.makeText(Update_Signup.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(Update_Signup.this,UserProfile2.class));
                                            finish();

                                        } else {
                                            Toast.makeText(Update_Signup.this, message, Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (Exception e) {
                                        Toast.makeText(Update_Signup.this, "Parse Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(Update_Signup.this, "Network Error", Toast.LENGTH_SHORT).show();
                                }
                            }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();
                            params.put("userName", name);
                            params.put("email", email);

                            // table name (dynamic)
                            params.put("table_name", "user_" + name.replace(" ", "_"));

                            return params;
                        }
                    };

                    RequestQueue queue = Volley.newRequestQueue(Update_Signup.this);
                    queue.add(request);

                } else {
                    Toast.makeText(Update_Signup.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alreadyLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Update_Signup.this,UpdateLogin.class));
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}