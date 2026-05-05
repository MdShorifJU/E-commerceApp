package com.shorif.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class UpdateLogin extends AppCompatActivity {

    EditText username, email;
    Button btnLogin;
    TextView tvCreateAccount,forgotPass;
    SharedPreferences sharedPreferences;

    ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_login);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        backImg = findViewById(R.id.backImg);

        forgotPass=findViewById(R.id.forgotPass);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        btnLogin.setOnClickListener(view -> {
            String name = username.getText().toString().trim();
            String mail = email.getText().toString().trim();

            if (name.isEmpty() || mail.isEmpty()) {
                Toast.makeText(UpdateLogin.this, "Fill up all information", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "https://dhakashoping.xyz/update_login.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, s -> {
                try {
                    JSONObject object = new JSONObject(s);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if (status.equals("success")) {
                        String table = object.optString("table_name");
                        Toast.makeText(UpdateLogin.this, message, Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("userName", name);
                        editor.putString("email", mail);
                        editor.putString("table", table);
                        editor.apply();

                        startActivity(new Intent(UpdateLogin.this, UserProfile2.class));
                        finish();
                    } else {
                        Toast.makeText(UpdateLogin.this, message, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(UpdateLogin.this, "Response error", Toast.LENGTH_SHORT).show();
                }


            }, volleyError -> Toast.makeText(UpdateLogin.this, "Network error", Toast.LENGTH_SHORT).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<>();

                    map.put("userName", name);
                    map.put("email", mail);

                    return map;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(UpdateLogin.this);
            queue.add(stringRequest);


        });
        tvCreateAccount.setOnClickListener(view -> startActivity(new Intent(UpdateLogin.this, Update_Signup.class)));


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateLogin.this,ForgotActivity.class));
            }
        });
    }
}