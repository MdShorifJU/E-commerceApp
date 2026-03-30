package com.shorif.e_commerce;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

public class FirstShowProfile extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    Button logoutBtn,updateBtn;
    TextView nameUser,phoneUser,gmailUser,passwordUser;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_show_profile);


        logoutBtn = findViewById(R.id.logoutBtn);
        passwordUser = findViewById(R.id.passwordUser);
        gmailUser = findViewById(R.id.gmailUser);
        phoneUser = findViewById(R.id.phoneUser);
        nameUser = findViewById(R.id.nameUser);
        updateBtn = findViewById(R.id.updateBtn);
        imageView = findViewById(R.id.imageView);

        sharedPreferences=getSharedPreferences("myApp",MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","");
        String password = sharedPreferences.getString("password","");

        try {
            MyMethods.my_key= MyMethods.encryptData("juba112233");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(phone.isEmpty() || password.isEmpty()){
            startActivity(new Intent(FirstShowProfile.this, Login_Activity.class));
            finish();
        }else{
            objectRequest();
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("phone","");
                editor.putString("password","");
                editor.apply();
                startActivity(new Intent(FirstShowProfile.this, Login_Activity.class));
                finish();
            }
        });
    }

    private void objectRequest(){
        String url="https://dhakashoping.xyz/profile/home.php";
        JSONObject jsonObject=new JSONObject();
        try{
            jsonObject.put("key", MyMethods.my_key);
            jsonObject.put(
                    "email",
                    MyMethods.encryptData(
                            sharedPreferences.getString("email","")
                    )
            );
            jsonObject.put("phone", sharedPreferences.getString("phone",""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                        response -> {
                            try {
                                nameUser.setText(response.getString("name"));
                                phoneUser.setText(response.getString("phone"));
                                gmailUser.setText(response.getString("gmail"));
                                passwordUser.setText(response.getString("pass"));

                                Picasso.get()
                                        .load(response.getString("image"))
                                        .placeholder(R.drawable.plus)
                                        .error(R.drawable.plus)
                                        .into(imageView);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> new AlertDialog.Builder(FirstShowProfile.this)
                                .setTitle("Server error")
                                .setMessage(error.toString())
                                .show()
                );

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

}