package com.shorif.e_commerce;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class All_Activity_1 extends AppCompatActivity {
    ImageView arrow_Icon;
    RecyclerView all_item_recycle;
    ZAdapter zAdapter;
    ArrayList<HashMap<String,String>> arrayList;
    HashMap<String,String> hashMap;
    FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all1);

        arrow_Icon = findViewById(R.id.arrow_Icon);
        all_item_recycle = findViewById(R.id.all_item_recycle);
        frameLayout = findViewById(R.id.frameLayout);

        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(All_Activity_1.this, R.color.teal_custom));

        arrow_Icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(All_Activity_1.this,MainActivity.class));
                finish();
            }
        });

        loadData3();
        zAdapter  = new ZAdapter();
        all_item_recycle.setAdapter(zAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(All_Activity_1.this,2);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        all_item_recycle.setLayoutManager(gridLayoutManager);

    }

    private void loadData3() {

        arrayList = new ArrayList<>();
        String url = "https://dhakashoping.xyz/CategoryList/all_category_list.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {

                                JSONObject jsonObject = response.getJSONObject(i);

                                String title = jsonObject.getString("title");
                                String image = jsonObject.getString("image");

                                hashMap = new HashMap<>();
                                hashMap.put("title", title);
                                hashMap.put("link", image);

                                arrayList.add(hashMap);
                            }

                            // Notify adapter after loading
                            zAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            //Nothing
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //nothing
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(All_Activity_1.this);
        queue.add(jsonArrayRequest);
    }

    public class ZAdapter extends RecyclerView.Adapter<ZAdapter.AllViewHolder> {

        @NonNull
        @Override
        public AllViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = getLayoutInflater();
            View myView3 = layoutInflater.inflate(R.layout.yitem, parent, false);
            return new AllViewHolder(myView3);
        }
        @Override
        public void onBindViewHolder(@NonNull AllViewHolder holder, int position) {
            hashMap = arrayList.get(position);

            // Set title
            holder.title_y.setText(hashMap.get("title"));

            // Load image using Picasso
            String fullImageUrl = "https://dhakashoping.xyz/CategoryList/" + hashMap.get("link");
            Picasso.get()
                    .load(fullImageUrl)
                    .into(holder.item3_img);


            holder.item3_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(position==1){
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frameLayout, new LaptopFragment());
                        fragmentTransaction.commit();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class AllViewHolder extends RecyclerView.ViewHolder {
            ImageView item3_img;
            TextView title_y;

            public AllViewHolder(@NonNull View itemView) {
                super(itemView);
                item3_img = itemView.findViewById(R.id.item3_img);
                title_y = itemView.findViewById(R.id.title_y);
            }
        }
    }
}