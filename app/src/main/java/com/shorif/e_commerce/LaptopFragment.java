package com.shorif.e_commerce;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LaptopFragment extends Fragment {

    private RecyclerView recyclerView;
    private LaptopAdapter laptopAdapter;
    private final ArrayList<Laptop> laptops = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_laptop, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);

        // LayoutManager FIRST

        int spanCount = calculateSpanCount();
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), spanCount);
        recyclerView.setLayoutManager(layoutManager);

        // Adapter
        laptopAdapter = new LaptopAdapter();
        recyclerView.setAdapter(laptopAdapter);

        loadData();

        return view;
    }

    private void loadData() {

        String url = "https://dhakashoping.xyz/Laptop/laptop.json";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                response -> {

                    laptops.clear();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);

                            Laptop laptop = new Laptop();
                            laptop.title = obj.getString("title");
                            laptop.image = obj.getString("Image");
                            laptop.cost = obj.getInt("cost");
                            laptop.save=obj.getInt("save");
                            JSONObject keyFeatures = obj.getJSONObject("keyFeatures");
                            laptop.model = keyFeatures.getString("model");
                            laptop.processor = keyFeatures.getString("processor");
                            laptop.ram = keyFeatures.getString("ram");
                            laptop.storage = keyFeatures.getString("storage");
                            laptop.feature = keyFeatures.getString("features");//৳


                            laptops.add(laptop);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    // 🔥 UPDATE UI
                    laptopAdapter.notifyDataSetChanged();
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }

    // ================= ADAPTER =================

    public class LaptopAdapter extends RecyclerView.Adapter<LaptopAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.laptop_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            Laptop laptop = laptops.get(position);


            holder.tvNewPrice.setText(calMoney(String.valueOf(laptop.cost)));
            holder.laptopRam.setText(laptop.ram);
            holder.laptopProcessor.setText(laptop.processor);
            holder.laptopFeatures.setText(laptop.feature);
            holder.title.setText(laptop.title);
            holder.tvSave.setText(calMoney(String.valueOf(laptop.save)));

            holder.tvOldPrice.setText(calMoney(String.valueOf(laptop.cost-laptop.save)));

            String imageUrl = "https://dhakashoping.xyz/Laptop/" + laptop.image;
            Picasso.get().load(imageUrl).into(holder.image);
        }

        @Override
        public int getItemCount() {
            return laptops.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView title, laptopProcessor, laptopRam, laptopFeatures,
                    tvNewPrice, tvOldPrice, tvSave;
            ImageView image;

            ViewHolder(@NonNull View itemView) {
                super(itemView);

                title = itemView.findViewById(R.id.titleItemLaptop);
                laptopProcessor = itemView.findViewById(R.id.laptop_Processor);
                laptopRam = itemView.findViewById(R.id.laptop_Ram);
                laptopFeatures = itemView.findViewById(R.id.laptop_Features);
                tvNewPrice = itemView.findViewById(R.id.tvNewPrice);
                tvOldPrice = itemView.findViewById(R.id.tvOldPrice);
                tvSave = itemView.findViewById(R.id.tvSave);
                image = itemView.findViewById(R.id.imageItemLaptop);
            }
        }
    }

    private int calculateSpanCount() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float dpWidth = metrics.widthPixels / metrics.density;

        int itemWidthDp = 180;
        return Math.max(2, (int) (dpWidth / itemWidthDp));
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

}
