package com.example.music_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_app.R;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiHatTheLoaiAdapter extends RecyclerView.Adapter<BaiHatTheLoaiAdapter.ViewHolder> {

    public interface OnSongClickListener {
        void onSongClick(BaiHat song, int position);
    }

    private final Context context;
    private final ArrayList<BaiHat> songs;
    private final Set<Integer> favoriteSongs = new HashSet<>();
    private final UserSessionManager sessionManager;
    private final DataService dataService;
    private final OnSongClickListener listener;

    public BaiHatTheLoaiAdapter(Context context, ArrayList<BaiHat> songs, OnSongClickListener listener) {
        this.context = context;
        this.songs = songs;
        this.listener = listener;
        this.sessionManager = new UserSessionManager(context);
        this.dataService = APIService.getService();
        loadFavorites();
    }

    private void loadFavorites() {
        if (!sessionManager.isLoggedIn()) return;

        int userId = sessionManager.getUserId();
        dataService.getFavorites(userId).enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteSongs.clear();
                    for (BaiHat s : response.body()) {
                        favoriteSongs.add(s.getId());
                    }
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e("BaiHatTheLoaiAdapter", "Lỗi load yêu thích: " + t.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiHat song = songs.get(position);
        holder.tvSongName.setText(song.getTenBaiHat());
        holder.tvSinger.setText(song.getCaSi());

        Glide.with(context)
                .load(song.getHinhAnh())
                .placeholder(R.drawable.music_placeholder)
                .into(holder.imgSong);

        updateHeartIcon(holder.imgLike, song.getId());
        holder.imgLike.setOnClickListener(v -> toggleFavorite(song, holder.imgLike));

        // Click vào bài hát
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSongClick(song, position); // gọi Activity mở MusicPlayer
            }
        });
    }

    private void updateHeartIcon(ImageView imgLike, int songId) {
        int color = favoriteSongs.contains(songId)
                ? ContextCompat.getColor(context, android.R.color.holo_red_dark)
                : ContextCompat.getColor(context, android.R.color.darker_gray);
        imgLike.setColorFilter(color);
    }

    private void toggleFavorite(BaiHat song, ImageView imgLike) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(context, "Vui lòng đăng nhập để yêu thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        int songId = song.getId();

        if (favoriteSongs.contains(songId)) {
            dataService.deleteFavorite(userId, songId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        favoriteSongs.remove(songId);
                        updateHeartIcon(imgLike, songId);
                        Toast.makeText(context, "💔 Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            dataService.addFavorite(userId, songId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        favoriteSongs.add(songId);
                        updateHeartIcon(imgLike, songId);
                        Toast.makeText(context, "❤️ Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songs != null ? songs.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSongName, tvSinger;
        ImageView imgSong, imgLike;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSongName = itemView.findViewById(R.id.tvTenBaiHat);
            tvSinger = itemView.findViewById(R.id.tvTenCaSi);
            imgSong = itemView.findViewById(R.id.imgBiaNhac);
            imgLike = itemView.findViewById(R.id.imgTimYeuThich);
        }
    }
}
