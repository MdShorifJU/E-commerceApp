package com.shorif.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CheckOutActivity extends AppCompatActivity {

    EditText etUserName, etEmail, etPhone, etAddress;
    Button btnPlaceOrder;
    public static boolean PAMENT_PROCESS= false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        String cost = getIntent().getStringExtra("cost");

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = etUserName.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();


                if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(CheckOutActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                    return;
                }

                String posData = "";
                try {
                    String ssName = URLEncoder.encode(name,"UTF-8");
                    String ssGmail = URLEncoder.encode(email,"UTF-8");
                    String ssPhone = URLEncoder.encode(phone,"UTF-8");
                    String ssAddress = URLEncoder.encode(address,"UTF-8");
                    String ssAmount = URLEncoder.encode(cost,"UTF-8");

                    posData = "amount="+ssAmount+"&name="+ssName+"&email="+ssGmail+"&address="+ssAddress+"&phone="+ssPhone;

                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                PamentWebview.WEB_POST_DATA=posData;
                startActivity(new Intent(CheckOutActivity.this,PamentWebview.class));
                Toast.makeText(CheckOutActivity.this, name+" "+email+" "+phone+" "+address, Toast.LENGTH_SHORT).show();
            }
        });
    }
}