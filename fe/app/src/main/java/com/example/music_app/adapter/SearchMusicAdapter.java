package com.example.music_app.adapter;

import android.content.Context;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.recyclerview.widget.RecyclerView;

import com.example.music_app.R;
import com.example.music_app.model.BaiHat;

import java.util.List;

public class SearchMusicAdapter extends RecyclerView.Adapter<SearchMusicAdapter.ViewHolder> {

    private Context context;
    private List<BaiHat> baiHatList;

    public interface OnItemClickListener {
        void onItemClick(BaiHat baiHat, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public SearchMusicAdapter(Context context, List<BaiHat> baiHatList) {
        this.context = context;
        this.baiHatList = baiHatList;
    }

    public void setData(List<BaiHat> list) {
        this.baiHatList = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiHat baiHat = baiHatList.get(position);
        holder.txtTenBaiHat.setText(baiHat.getTenBaiHat());
        holder.txtCaSi.setText(baiHat.getCaSi());


        Log.d("GLIDE_TEST", "Đang load ảnh: " + baiHat.getHinhAnh());

        // Nếu bạn muốn hiển thị ảnh bài hát
        Glide.with(context)
                .load(baiHat.getHinhAnh())
                .into(holder.imgBaiHat);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(baiHat, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return baiHatList != null ? baiHatList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTenBaiHat, txtCaSi;
        ImageView imgBaiHat ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBaiHat = itemView.findViewById(R.id.imgSong);
            txtTenBaiHat = itemView.findViewById(R.id.tvSongName);
            txtCaSi = itemView.findViewById(R.id.tvArtist);
        }
    }
}