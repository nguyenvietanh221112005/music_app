package com.example.music_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.music_app.R;
import com.example.music_app.activity.CategoryDetailActivity;
import com.example.music_app.model.TheLoai;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private static final String TAG = "CategoryAdapter";
    Context context;
    ArrayList<TheLoai> list;
    private OnCategoryClickListener onCategoryClickListener;

    public interface OnCategoryClickListener {
        void onCategoryClick(TheLoai idTheLoai);
    }

    public CategoryAdapter(Context context, ArrayList<TheLoai> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.onCategoryClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TheLoai tl = list.get(position);
        holder.tvName.setText(tl.getTenTheLoai());

        String imageData = tl.getHinhAnh();
        Log.d(TAG, "Loading image for category: " + tl.getTenTheLoai());

        if (imageData == null || imageData.isEmpty()) {
            Log.e(TAG, "Image data is null or empty");
            holder.imgCategory.setImageResource(R.drawable.default_category);
        } else {
            Log.d(TAG, "Image data length: " + imageData.length());


            if (imageData.startsWith("data:image")) {
                loadBase64Image(holder.imgCategory, imageData, tl.getTenTheLoai());
            }

            else if (imageData.startsWith("http://") || imageData.startsWith("https://")) {
                loadUrlImage(holder.imgCategory, imageData);
            }

            else if (imageData.startsWith("/")) {
                String fullUrl = "http://192.168.1.7:8080" + imageData;
                loadUrlImage(holder.imgCategory, fullUrl);
            }
            else {
                Log.e(TAG, "Unknown image format");
                holder.imgCategory.setImageResource(R.drawable.default_category);
            }
        }


        holder.itemView.setOnClickListener(v -> {
            if (onCategoryClickListener != null) {

                onCategoryClickListener.onCategoryClick(tl);
            }
        });
    }

    private void loadBase64Image(ImageView imageView, String base64Data, String categoryName) {
        try {
            Log.d(TAG, "Attempting to load base64 image for: " + categoryName);

            String base64Image;
            if (base64Data.contains(",")) {
                base64Image = base64Data.split(",")[1];
            } else {
                base64Image = base64Data;
            }


            base64Image = base64Image.replaceAll("\\s+", "");

            Log.d(TAG, "Cleaned base64 length: " + base64Image.length());


            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            Log.d(TAG, "Decoded bytes length: " + decodedBytes.length);


            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Log.d(TAG, "✅ Successfully loaded bitmap: " + bitmap.getWidth() + "x" + bitmap.getHeight());
                imageView.setImageBitmap(bitmap);
            } else {
                Log.e(TAG, "❌ Bitmap is null after decoding");
                imageView.setImageResource(R.drawable.default_category);
            }

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "❌ Base64 decode error: " + e.getMessage());
            e.printStackTrace();
            imageView.setImageResource(R.drawable.default_category);
        } catch (Exception e) {
            Log.e(TAG, "❌ General error loading base64: " + e.getMessage());
            e.printStackTrace();
            imageView.setImageResource(R.drawable.default_category);
        }
    }

    private void loadUrlImage(ImageView imageView, String url) {
        Log.d(TAG, "Loading URL image: " + url);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.default_category)
                .error(R.drawable.default_category)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}