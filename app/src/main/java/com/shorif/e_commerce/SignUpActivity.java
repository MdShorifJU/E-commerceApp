package com.shorif.e_commerce;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    TextInputEditText edittextName, edittextEmail, edittextPhone, edittextPassword, edittextPasswordConfirm;
    TextInputLayout layoutPassword ,layoutPhone,layoutEmail,layoutPasswordConfirm;
    TextView imageFromUser;
    Button btnSignup, btnLogin;
    ImageView imageView;

    Bitmap bitmap = null;
    String imgStr = "";

    ActivityResultLauncher<Intent> pickImages;
    FirebaseAuth auth;
    private static final String PASSWORD_PATTERN =
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{6,}$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edittextName = findViewById(R.id.edittextName);
        edittextEmail = findViewById(R.id.edittextEmail);
        edittextPhone = findViewById(R.id.edittextPhone);
        edittextPassword = findViewById(R.id.edittextPassword);
        edittextPasswordConfirm = findViewById(R.id.edittextPasswordConfirm);

        layoutPassword = findViewById(R.id.layoutPassword);
        layoutPhone=findViewById(R.id.layoutPhone);
        layoutEmail=findViewById(R.id.layoutEmail);
        layoutPasswordConfirm=findViewById(R.id.layoutPasswordConfirm);
        imageFromUser = findViewById(R.id.imageFromUser);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        imageView = findViewById(R.id.imageView);

        auth = FirebaseAuth.getInstance();
        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(SignUpActivity.this, Login_Activity.class))
        );

        pickImages = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK &&
                            result.getData() != null &&
                            result.getData().getData() != null) {

                        Uri imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                        uriToBitmap(imageUri);
                    } else {
                        Toast.makeText(this, "Image not selected", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        imageFromUser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImages.launch(intent);
        });

        btnSignup.setOnClickListener(v -> signupUserFinal());

    }
    private void signupUserFinal() {
        String name = edittextName.getText().toString().trim();
        String email = edittextEmail.getText().toString().trim();
        String phone = edittextPhone.getText().toString().trim();
        String pass = edittextPassword.getText().toString().trim();
        String conPass = edittextPasswordConfirm.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }
        if(email.isEmpty()){
            layoutEmail.setError("Required");
            return;
        }else {
            layoutEmail.setError(null);
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            layoutEmail.setError("Please enter a valid email address");
            return;
        }else {
            layoutEmail.setError(null);
        }
        String PHONE_PATTERN = "^(?:\\+?88)?01[3-9][0-9]{8}$";

        if (phone.isEmpty()) {
            layoutPhone.setError("Phone required");
            return;
        }else {
            layoutPhone.setError(null);
        }
        if (!phone.matches(PHONE_PATTERN) ){
            layoutPhone.setError("Enter a valid Bangladeshi number");
            return;
        }else {
            layoutPhone.setError(null);
        }

        if (pass.isEmpty()) {
            layoutPassword.setError("Password required");
            return;
        }else {
            layoutPassword.setError(null);
        }
        if (pass.length() < 6) {
            layoutPassword.setError("Minimum 6 characters");
            return;
        }else {
            layoutPassword.setError(null);
        }
        if (!pass.matches(PASSWORD_PATTERN)) {
            layoutPassword.setError(
                    "Use A–Z, a–z, 0–9 & special character"
            );
            return;
        }else {
            layoutPassword.setError(null);
        }

        if (!pass.equals(conPass)) {
            layoutPasswordConfirm.setError("Password not matching");
            return;
        }else {
            layoutPasswordConfirm.setError(null);
        }
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        imgStr = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);

        String url = "https://dhakashoping.xyz/profile/signUp.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equalsIgnoreCase("Signup Success")) {
                        SharedPreferences.Editor editor =
                                getSharedPreferences("myApp", MODE_PRIVATE).edit();
                        editor.putString("email", email);
                        editor.putString("phone", phone);
                        editor.putString("password",pass);
                        editor.apply();
                        Toast.makeText(this, "Account Create Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, FirstShowProfile.class));
                        finish();
                    } else {
                        showErrorDialog(response);

                    }
                },
                error -> showErrorDialog("Server Error")
        ) {
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                try {
                    map.put("email", MyMethods.encryptData(email));
                    map.put("phone", MyMethods.encryptData(phone));
                    map.put("password", MyMethods.encryptData(pass));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                map.put("name", name);
                map.put("key", MyMethods.my_key);
                map.put("image", imgStr);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void showErrorDialog(String title) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Unable to connect\nTry again")
                .setPositiveButton("OK", null)
                .show();
    }
    private void uriToBitmap(Uri imageUri) {
        try {
            ContentResolver resolver = getContentResolver();
            InputStream inputStream = resolver.openInputStream(imageUri);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
