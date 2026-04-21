package com.shorif.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashMap;

public class Admin_Activity extends AppCompatActivity {

    ListView listView;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        listView = findViewById(R.id.listView);

        loadData();

        listView.setAdapter(new Admin_class());
    }

    public void loadData() {
        arrayList = new ArrayList<>();
        hashMap = new HashMap<>();
        hashMap.put("icon", "analytics");
        hashMap.put("title", "Analytics");
        arrayList.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("icon", "promotions");
        hashMap.put("title", "Promotions");
        arrayList.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("icon", "trackingown");
        hashMap.put("title", "order Tracking");
        arrayList.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("icon", "trackingpublic");
        hashMap.put("title", "All Order Tracking");
        arrayList.add(hashMap);

        arrayList.add(hashMap);
        hashMap = new HashMap<>();
        hashMap.put("icon", "plus");
        hashMap.put("title", "Product Management");
        arrayList.add(hashMap);
    }

    private class Admin_class extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

            if (view == null) {
                view = LayoutInflater.from(Admin_Activity.this)
                        .inflate(R.layout.list_item, parent, false);
            }

            ImageView admin_icon = view.findViewById(R.id.admin_icon);
            TextView admin_title = view.findViewById(R.id.admin_title);
            CardView admin_card = view.findViewById(R.id.admin_card);

            HashMap<String, String> map = arrayList.get(i);

            int resId = getResources().getIdentifier(
                    map.get("icon"),
                    "drawable",
                    getPackageName()
            );

            admin_icon.setImageResource(resId);
            admin_title.setText(map.get("title"));

            admin_card.setOnClickListener(v -> {

                switch (i) {
                    case 0:
                        startActivity(new Intent(Admin_Activity.this, AnalyticsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(Admin_Activity.this, PromotionActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(Admin_Activity.this, OrderTrackingActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(Admin_Activity.this, AllOrderActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(Admin_Activity.this, CategoryActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(Admin_Activity.this, ProductActivity.class));
                        break;
                }

            });

            return view;
        }
    }
}