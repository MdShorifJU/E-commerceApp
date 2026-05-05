package com.shorif.e_commerce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UserProfile2 extends AppCompatActivity {

    TextView name, userName, userEmail, userProfession;
    LinearLayout userLogout, userEditProfile;
    SharedPreferences sharedPreferences;
    ImageView userImage;

    ImageView ivProfileDialog;
    Bitmap selectedBitmap = null;

    // Gallery launcher
    ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            // URI to Bitmap
                            selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), imageUri);

                            if (ivProfileDialog != null) {
                                ivProfileDialog.setImageBitmap(selectedBitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(UserProfile2.this,
                                    "Image load failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile2);

        name = findViewById(R.id.name);
        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);
        userProfession = findViewById(R.id.userProfession);
        userEditProfile = findViewById(R.id.userEditProfile);
        userLogout = findViewById(R.id.userLogout);
        userImage = findViewById(R.id.userImage);

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);

        updateUI();

        // Logout portion
        userLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userName", null);
                editor.putString("email", null);
                editor.putString("table", null);
                editor.apply();
                startActivity(new Intent(UserProfile2.this, UpdateLogin.class));
                finish();
            }
        });

        //============= Profile===============================
        userEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedBitmap = null;

                AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile2.this);
                View myView = LayoutInflater.from(UserProfile2.this).inflate(R.layout.profileitem, null);
                builder.setView(myView);
                AlertDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();

                ivProfileDialog = myView.findViewById(R.id.ivProfile);
                EditText etName = myView.findViewById(R.id.etName);
                EditText etEmail = myView.findViewById(R.id.etEmail);
                EditText etProfession = myView.findViewById(R.id.etProfession);
                Button btnCancel = myView.findViewById(R.id.btnCancel);
                Button btnSubmit = myView.findViewById(R.id.btnSubmit);


                //previous data load
                etName.setText(sharedPreferences.getString("userName", ""));
                etEmail.setText(sharedPreferences.getString("email", ""));
                etProfession.setText(sharedPreferences.getString("profession", ""));

                String savedImage = sharedPreferences.getString("imageBitmap", null);
                if (savedImage != null) {
                    Bitmap bmp = base64ToBitmap(savedImage);
                    ivProfileDialog.setImageBitmap(bmp);
                }

                //Image from gallery
                ivProfileDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        galleryLauncher.launch(intent);
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ivProfileDialog = null;
                        dialog.dismiss();
                    }
                });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nameVal = etName.getText().toString().trim();
                        String emailVal = etEmail.getText().toString().trim();
                        String professionVal = etProfession.getText().toString().trim();

                        boolean ok = true;
                        if (nameVal.isEmpty()) {
                            etName.setError("Name দিন");
                            ok=false;
                        }
                        if (emailVal.isEmpty()) {
                            etEmail.setError("Email দিন");
                            ok=false;
                        }
                        if (professionVal.isEmpty()) {
                            etProfession.setError("Profession দিন");
                            ok=false;
                        }

                        if(!ok){
                            return;
                        }

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName", nameVal);
                        editor.putString("email", emailVal);
                        editor.putString("profession", professionVal);


                        if (selectedBitmap != null) {
                            String base64Image = bitmapToBase64(selectedBitmap);
                            editor.putString("imageBitmap", base64Image);
                        }

                        editor.apply();
                        updateUI();
                        ivProfileDialog = null;
                        dialog.dismiss();
                        Toast.makeText(UserProfile2.this,
                                "Profile updated!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    public void updateUI() {
        String n = sharedPreferences.getString("userName", "");
        String e = sharedPreferences.getString("email", "");
        String p = sharedPreferences.getString("profession", "");
        String imgBase64 = sharedPreferences.getString("imageBitmap", null);

        userName.setText(n);
        userEmail.setText(e);
        userProfession.setText(p);

        if (imgBase64 != null) {
            Bitmap bmp = base64ToBitmap(imgBase64);
            userImage.setImageBitmap(bmp);
        }
    }

    private String bitmapToBase64(Bitmap bitmap) {
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] bytes = baos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private Bitmap base64ToBitmap(String base64Str) {
        byte[] bytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}