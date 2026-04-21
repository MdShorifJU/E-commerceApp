package com.shorif.e_commerce;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class CartList extends AppCompatActivity {

    RecyclerView recyclerView;
    DataBaseHelper dataBaseHelper;

    ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    CartAdapter cartAdapter;
    TextView total_price,checkout_btn;
    int total_cost=0;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        recyclerView = findViewById(R.id.recyclerView);
        dataBaseHelper = new DataBaseHelper(this);
        total_price = findViewById(R.id.total_price);
        checkout_btn = findViewById(R.id.checkout_btn);


        loadData();

        total_price.setText(calMoney(String.valueOf(total_cost)));

        cartAdapter = new CartAdapter();
        recyclerView.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(this)
        );
        recyclerView.setAdapter(cartAdapter);

        cartAdapter.notifyDataSetChanged();

        checkout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartList.this, CheckOutActivity.class);
                intent.putExtra("cost",total_cost);
                startActivity(intent);
            }
        });
    }

    private void loadData() {


        arrayList.clear();
        total_cost = 0;
        Cursor cursor = dataBaseHelper.getAllData();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id",cursor.getString(0));
                hashMap.put("title", cursor.getString(1));
                hashMap.put("price", cursor.getString(2));
                hashMap.put("image", cursor.getString(3));
                hashMap.put("tv_quantity", cursor.getString(4));
                hashMap.put("product_id",cursor.getString(5));

                int price = calculate(cursor.getString(2));
                int qty = Integer.parseInt(cursor.getString(4));

                total_cost = total_cost + (price * qty);
                arrayList.add(hashMap);


            }
            cursor.close();
        }
    }

    // ================= Adapter =================
    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(CartList.this)
                    .inflate(R.layout.cartitem, parent, false);

            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            HashMap<String,String> hashMap = arrayList.get(position);

            String title=hashMap.get("title");
            String price = hashMap.get("price");
            String product_id=hashMap.get("product_id");

            holder.title.setText(title);
            holder.price.setText(price);
            holder.tv_quantity.setText(hashMap.get("tv_quantity"));
            String url = hashMap.get("image");


            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.cart)
                    .into(holder.image);



            holder.btn_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataBaseHelper.plusMethod(product_id);
                    updateUI();
                }
            });

            holder.btn_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dataBaseHelper.minusMethod(product_id);
                    updateUI();
                }
            });

            holder.btn_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CartList.this);
                    View myView = LayoutInflater.from(CartList.this).inflate(R.layout.threedotsfunction, null);
                    builder.setView(myView);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    TextView delete = myView.findViewById(R.id.delete);
                    TextView share = myView.findViewById(R.id.share);
                    TextView copy = myView.findViewById(R.id.copy);


                    copy.setOnClickListener(v -> {
                        try {
                            String textToCopy = "Title: " + title + "\nPrice: " + price ;
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData data = ClipData.newPlainText("Info", textToCopy);
                            clipboard.setPrimaryClip(data);
                            Toast.makeText(CartList.this, "Copied", Toast.LENGTH_SHORT).show();
                        } finally {
                            dialog.dismiss();
                        }
                    });
                    share.setOnClickListener(v -> {
                        try {
                            String textToShare = "Title: " + title + "\nPrice: " + price ;
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                            Intent chooser = Intent.createChooser(shareIntent, "Sharing");
                            startActivity(chooser);
                        } finally {
                            dialog.dismiss();
                        }
                    });
                    delete.setOnClickListener(v -> {
                        String message = dataBaseHelper.deleteById(product_id);
                        updateUI();
                        Toast.makeText(CartList.this, message, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }
            });

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView image,btn_menu;
            TextView title, price,btn_minus,btn_plus,tv_quantity;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                image = itemView.findViewById(R.id.root_img);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);
                btn_plus = itemView.findViewById(R.id.btn_plus);
                btn_minus = itemView.findViewById(R.id.btn_minus);
                tv_quantity = itemView.findViewById(R.id.tv_quantity);
                btn_menu = itemView.findViewById(R.id.btn_menu);
            }
        }
    }

    private int calculate(String money) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < money.length(); i++) {

            char ch = money.charAt(i);
            if (ch != ',' && ch != '৳' && ch != 't') {
                stringBuilder.append(ch);
            }
        }

        return Integer.parseInt(stringBuilder.toString());
    }

    public String calMoney(String taka) {
        StringBuilder result = new StringBuilder();
        int count = 0;
        result.append("৳");
        for (int i = taka.length() - 1; i >= 0; i--) {
            result.append(taka.charAt(i));
            count++;

            if (count == 3 && i != 0) {
                result.append(",");
                count = 0;
            }
        }

        return result.reverse().toString();
    }

    private void updateUI() {
        loadData();
        if (cartAdapter != null) {
            cartAdapter.notifyDataSetChanged();
        }
        total_price.setText(calMoney(String.valueOf(total_cost)));
    }
}