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
    private final Set<Integer> favoriteSongs = new HashSet<>();//Set chứa id của các bài đã yêu thích. Dùng để quyết định màu icon tim.
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
        //tức là khi adapter được tạo sẽ tự gọi API lấy danh sách favorite của user (nếu đã đăng nhập) và điền favoriteSongs.
        // Khi dữ liệu favorite về, adapter gọi notifyDataSetChanged() để refresh UI (đổi màu tim tương ứng).
    }


    //lấy danh sách favorite ban đầu
    private void loadFavorites() {
        if (!sessionManager.isLoggedIn()) return;

        int userId = sessionManager.getUserId();
        dataService.getFavorites(userId).enqueue(new Callback<ArrayList<BaiHat>>() {//gọi API lấy danh sách bai hát yêu thích
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {//nếu có dữ liệu trả về
                    favoriteSongs.clear();//Xóa toàn bộ dữ liệu yêu thích cũ trong bộ nhớ tạm (Set)
                                            // sau đó cập nhật lại hoàn toàn bằng dữ liệu mới từ server
                    for (BaiHat s : response.body()) {
                        favoriteSongs.add(s.getId());//Thêm các ID bài hát mới được server trả về
                    }
                    notifyDataSetChanged();//gọi hàm này để RecyclerView cập nhật
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e("BaiHatTheLoaiAdapter", "Lỗi load yêu thích: " + t.getMessage());
            }
        });
    }


    //Tạo giao diện (layout) cho một item trong danh sách bằng cách đọc file item_list_category.xml.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_category, parent, false);
        return new ViewHolder(view);
    }


    //gán dữ liệu cho từng item
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


    //đổi màu trái tim
    private void updateHeartIcon(ImageView imgLike, int songId) {
        int color = favoriteSongs.contains(songId)//nếu songID có trong danh sách set
                ? ContextCompat.getColor(context, android.R.color.holo_red_dark)//đổi màu trái tim
                : ContextCompat.getColor(context, android.R.color.darker_gray);
        imgLike.setColorFilter(color);
    }


    //thêm / xóa yêu thích
    private void toggleFavorite(BaiHat song, ImageView imgLike) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(context, "Vui lòng đăng nhập để yêu thích bài hát", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        int songId = song.getId();

        if (favoriteSongs.contains(songId)) {//nếu đã yêu thích thì gọi xóa yêu thích
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
            dataService.addFavorite(userId, songId).enqueue(new Callback<Void>() {//nếu chưa yêu thích thì gọi thêm yêu thích
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

    public static class ViewHolder extends RecyclerView.ViewHolder {//Giữ tham chiếu đến các View con bên trong mỗi item của RecyclerView
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