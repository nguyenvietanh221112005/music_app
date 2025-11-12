package com.example.music_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

public class TopSongAdapter extends RecyclerView.Adapter<TopSongAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BaiHat> songList;
    private OnSongClickListener onSongClickListener;
    private Set<Integer> favoriteSongs = new HashSet<>();
    private UserSessionManager sessionManager;


    public interface OnSongClickListener {
        void onSongClick(BaiHat baiHat, int position);
    }

    public TopSongAdapter(Context context, ArrayList<BaiHat> songList) {
        this.context = context;
        this.songList = songList;
        if (context != null) {
            this.sessionManager = new UserSessionManager(context);
            loadFavorites();
        }
    }

    public void setOnSongClickListener(OnSongClickListener listener) {
        this.onSongClickListener = listener;
    }

    private void loadFavorites() {
        if (sessionManager == null || !sessionManager.isLoggedIn()) return;

        int userId = sessionManager.getUserId();
        DataService dataService = APIService.getService();

        dataService.getFavorites(userId).enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteSongs.clear();
                    for (BaiHat song : response.body()) {
                        favoriteSongs.add(song.getId());
                    }
                    notifyDataSetChanged();
                    Log.d("TopSongAdapter", "Loaded " + favoriteSongs.size() + " favorites");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e("TopSongAdapter", "Load favorites failed: " + t.getMessage());
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_top_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiHat song = songList.get(position);


        if (holder.tvSongName != null) {
            holder.tvSongName.setText(song.getTenBaiHat());
        }
        if (holder.tvSinger != null) {
            String caSi = song.getCaSi();
            if (caSi != null && !caSi.trim().isEmpty()) {
                holder.tvSinger.setText(caSi);
                Log.d("TopSongAdapter", "Set singer: " + caSi);
            } else {
                holder.tvSinger.setText("Chưa rõ ca sĩ");
            }
        }

        if (holder.imgSong != null) {
            Glide.with(context)
                    .load(song.getHinhAnh())
                    .placeholder(R.drawable.default_category)
                    .error(R.drawable.default_category)
                    .into(holder.imgSong);
        }

        if (holder.imgHeart != null) {
            updateFavoriteIcon(holder.imgHeart, song.getId());
        }

        if (holder.frameHeart != null) {

            holder.frameHeart.setOnClickListener(null);

            holder.frameHeart.setOnClickListener(v -> {
                Log.d("TopSongAdapter", "Heart clicked for: " + song.getTenBaiHat());
                toggleFavorite(song.getId(), holder.imgHeart);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Log.d("TopSongAdapter", "Item clicked: " + song.getTenBaiHat() + " at position " + position);
            if (onSongClickListener != null) {
                onSongClickListener.onSongClick(song, position);
            }
        });
    }

    private void updateFavoriteIcon(ImageView imgFavorite, int songId) {
        if (favoriteSongs.contains(songId)) {
            imgFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            Log.d("TopSongAdapter", "Song " + songId + " is favorite (RED)");
        } else {
            imgFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
            Log.d("TopSongAdapter", "Song " + songId + " is not favorite (GRAY)");
        }
    }

    private void toggleFavorite(int songId, ImageView imgFavorite) {
        if (sessionManager == null || !sessionManager.isLoggedIn()) {
            Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        DataService dataService = APIService.getService();

        if (favoriteSongs.contains(songId)) {
            Log.d("TopSongAdapter", "Removing favorite: " + songId);
            dataService.deleteFavorite(userId, songId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        favoriteSongs.remove(songId);
                        updateFavoriteIcon(imgFavorite, songId);
                        Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                        Log.d("TopSongAdapter", "Delete favorite SUCCESS");
                    } else {
                        Toast.makeText(context, "Lỗi xóa yêu thích: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("TopSongAdapter", "Delete favorite FAILED: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("TopSongAdapter", "Delete favorite ERROR: " + t.getMessage());
                }
            });
        } else {
            Log.d("TopSongAdapter", "Adding favorite: " + songId);
            dataService.addFavorite(userId, songId).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        favoriteSongs.add(songId);
                        updateFavoriteIcon(imgFavorite, songId);
                        Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                        Log.d("TopSongAdapter", "Add favorite SUCCESS");
                    } else {
                        Toast.makeText(context, "Lỗi thêm yêu thích: " + response.code(), Toast.LENGTH_SHORT).show();
                        Log.e("TopSongAdapter", "Add favorite FAILED: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("TopSongAdapter", "Add favorite ERROR: " + t.getMessage());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return songList != null ? songList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong, imgHeart;
        TextView tvSongName, tvSinger;
        FrameLayout frameHeart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgSong);
            imgHeart = itemView.findViewById(R.id.imgHeart);
            tvSongName = itemView.findViewById(R.id.tvSongName);
            tvSinger = itemView.findViewById(R.id.tvSinger);
            frameHeart = itemView.findViewById(R.id.frameHeart);
        }
    }
}