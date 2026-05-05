package com.shorif.e_commerce;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.AnimationTypes;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView sell_all1, sell_all2;
    RecyclerView recycler_all_category, recycler_popular;
    XAdapter xAdapter;
    ImageSlider imageSlider;
    ArrayList<SlideModel> slideModels = new ArrayList<>();
    ArrayList<String> imageList = new ArrayList<>();
    YAdapter yAdapter;

    ArrayList<HashMap<String, String>> all_arrayList;
    ArrayList<HashMap<String, String>> arrayList;
    HashMap<String, String> hashMap;
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences ;
    BadgeDrawable badge;



    DataBaseHelper dataBaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.teal_custom));
        window.setNavigationBarColor(ContextCompat.getColor(MainActivity.this, R.color.teal_custom));

        sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);


        sell_all1 = findViewById(R.id.sell_all1);
        sell_all2 = findViewById(R.id.sell_all2);
        recycler_all_category = findViewById(R.id.recycler_all_category);
        recycler_popular = findViewById(R.id.recycler_popular);
        imageSlider = findViewById(R.id.imageSlider);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        badge = bottomNavigationView.getOrCreateBadge(R.id.wishlist);
        dataBaseHelper=new DataBaseHelper(MainActivity.this);

        updateUI();
        load_Image_data();

        loadData();
        loadPopular();

        xAdapter = new XAdapter();
        recycler_all_category.setAdapter(xAdapter);
        recycler_all_category.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        int spaceCnt= calculateSpanCount();
        yAdapter = new YAdapter();
        recycler_popular.setAdapter(yAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, spaceCnt);
        recycler_popular.setLayoutManager(gridLayoutManager);


        sell_all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, All_Activity_1.class));
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.profile) {
                    String email = sharedPreferences.getString("email", null);
                    if (email != null && !email.isEmpty()) {
                        startActivity(new Intent(MainActivity.this, UserProfile2.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, UpdateLogin.class));
                    }
                } else if (id == R.id.wishlist) {
                    startActivity(new Intent(MainActivity.this, CartList.class));
                }
                return true;
            }
        });
    }


    private int calculateSpanCount() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float dpWidth = metrics.widthPixels / metrics.density;

        int itemWidthDp = 180;
        return Math.max(2, (int) (dpWidth / itemWidthDp));
    }
    private void loadPopular() {
        arrayList = new ArrayList<>();
        String url = "https://dhakashoping.xyz/CategoryList/popular/popularItem.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {

                int size = jsonArray.length();
                for (int i = 0; i < size; i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String title = jsonObject.getString("title");
                        String price = jsonObject.getString("price");
                        String img = jsonObject.getString("Image");
                        String product_id = jsonObject.getString("product_id");


                        hashMap = new HashMap<>();
                        hashMap.put("title",title);
                        hashMap.put("price",price);
                        hashMap.put("img",img);
                        hashMap.put("product_id",product_id);

                        arrayList.add(hashMap);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                yAdapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(jsonArrayRequest);

    }

    private void load_Image_data() {
        String url = "https://dhakashoping.xyz/image_slider/image.json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("images");

                            imageList.clear();  // optional, ensure empty first
                            for (int i = 0; i < jsonArray.length(); i++) {
                                imageList.add(jsonArray.getString(i));
                            }

                            // Add to slideModels after list is filled
                            slideModels.clear();
                            for (String url : imageList) {
                                slideModels.add(new SlideModel(url, ScaleTypes.FIT));
                            }

                            // Set to slider
                            imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                            imageSlider.setSlideAnimation(AnimationTypes.ZOOM_OUT);

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }


    private void loadData() {
        all_arrayList = new ArrayList<>();
        hashMap = new HashMap<>();
        hashMap.put("name", "mobile");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "laptop");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "camera");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "tv");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "stove");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "fan");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "female");
        all_arrayList.add(hashMap);

        hashMap = new HashMap<>();
        hashMap.put("name", "male");
        all_arrayList.add(hashMap);
    }

    private class XAdapter extends RecyclerView.Adapter<XAdapter.ImageFilterViewHolder> {

        class ImageFilterViewHolder extends RecyclerView.ViewHolder {
            ImageView img_list1;

            public ImageFilterViewHolder(@NonNull View itemView) {
                super(itemView);
                img_list1 = itemView.findViewById(R.id.img_list1);
            }
        }

        @NonNull
        @Override
        public ImageFilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View myView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.xitem, parent, false);
            return new ImageFilterViewHolder(myView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageFilterViewHolder holder, int position) {
            hashMap = all_arrayList.get(position);

            int resId = getResources().getIdentifier(hashMap.get("name"), "drawable", getPackageName());
            holder.img_list1.setImageResource(resId);
        }

        @Override
        public int getItemCount() {
            return all_arrayList.size();
        }
    }

    private class YAdapter extends RecyclerView.Adapter<YAdapter.ImageFilterViewHolder2> {

        @NonNull
        @Override
        public ImageFilterViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View myView2 = layoutInflater.inflate(R.layout.item, parent, false);
            return new ImageFilterViewHolder2(myView2);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageFilterViewHolder2 holder, int position) {

            hashMap=arrayList.get(position);

            String price = calMoney(Objects.requireNonNull(hashMap.get("price")));
            String title= hashMap.get("title");
            String product_id=hashMap.get("product_id");
            holder.price.setText(price);

            holder.title.setText(title);

            String fullImageUrl = "https://dhakashoping.xyz/CategoryList/popular/" + hashMap.get("img");

            Picasso.get()
                    .load(fullImageUrl)
                    .into(holder.root_img);



            holder.love_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String savedName = sharedPreferences.getString("userName", null);
                    String savedEmail = sharedPreferences.getString("email", null);

                    if (savedName != null && savedEmail != null
                            && !savedName.trim().isEmpty()
                            && !savedEmail.trim().isEmpty()) {

                        Toast.makeText(MainActivity.this,
                                "Successfully added to Favorite",
                                Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(MainActivity.this,
                                "Please create an account!!",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.plus_icon.setOnClickListener(view -> {

                String savedName = sharedPreferences.getString("userName", null);
                String savedEmail = sharedPreferences.getString("email", null);

                if (savedName != null && savedEmail != null
                        && !savedName.trim().isEmpty()
                        && !savedEmail.trim().isEmpty()) {

                    String message = dataBaseHelper.storeData(title, price, fullImageUrl,product_id);

                    Toast.makeText(MainActivity.this,
                            message,
                            Toast.LENGTH_SHORT).show();
                    updateUI();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Please create an account!!",
                            Toast.LENGTH_SHORT).show();
                }
            });


        }

        @Override
        public int getItemCount() {
            return arrayList == null ? 0 : arrayList.size();
        }

        class ImageFilterViewHolder2 extends RecyclerView.ViewHolder {

            ImageView root_img, love_img;
            TextView title, price, plus_icon;



            public ImageFilterViewHolder2(@NonNull View itemView) {
                super(itemView);

                root_img = itemView.findViewById(R.id.root_img);
                love_img = itemView.findViewById(R.id.love_img);
                plus_icon = itemView.findViewById(R.id.plus_icon);
                title = itemView.findViewById(R.id.title);
                price = itemView.findViewById(R.id.price);

            }
        }
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
    public void updateUI(){
        badge.setNumber(dataBaseHelper.getCartCount());
        badge.setVisible(true);
        badge.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        badge.setBadgeTextColor(getResources().getColor(android.R.color.white));
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}