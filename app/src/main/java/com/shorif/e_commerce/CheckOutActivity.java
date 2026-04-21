package com.shorif.e_commerce;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;


public class CheckOutActivity extends AppCompatActivity {

    EditText etUserName,etEmail,etPhone,etAddress;
    Button btnPlaceOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        etUserName = findViewById(R.id.etUserName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        String cost= getIntent().getStringExtra("cost");
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);


        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=etUserName.getText().toString();
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String address = etAddress.getText().toString();
            }
        });



    }
}