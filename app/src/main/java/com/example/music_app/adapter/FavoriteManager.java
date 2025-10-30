package com.example.music_app.adapter;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteManager {
    private static final String TAG = "FavoriteManager";

    private Context context;
    private UserSessionManager sessionManager;
    private FavoriteListener listener;
    private boolean isFavorite = false;

    public interface FavoriteListener {
        void onFavoriteStatusChanged(boolean isFavorite);
    }

    public FavoriteManager(Context context, FavoriteListener listener) {
        this.context = context;
        this.listener = listener;
        this.sessionManager = new UserSessionManager(context);
    }

    public void checkFavoriteStatus(BaiHat song) {
        if (!sessionManager.isLoggedIn() || song == null) {
            updateFavoriteStatus(false);
            return;
        }

        int userId = sessionManager.getUserId();
        DataService dataService = APIService.getService();

        dataService.getFavorites(userId).enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isFavorite = false;
                    for (BaiHat favSong : response.body()) {
                        if (favSong.getId() == song.getId()) {
                            isFavorite = true;
                            break;
                        }
                    }
                    updateFavoriteStatus(isFavorite);
                    Log.d(TAG, "Favorite status: " + isFavorite);
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e(TAG, "Failed to check favorite: " + t.getMessage());
                updateFavoriteStatus(false);
            }
        });
    }

    public void toggleFavorite(BaiHat song) {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(context, "Vui lòng đăng nhập để sử dụng tính năng này", Toast.LENGTH_SHORT).show();
            return;
        }

        if (song == null) return;

        int userId = sessionManager.getUserId();
        int songId = song.getId();
        DataService dataService = APIService.getService();

        if (isFavorite) {
            removeFavorite(dataService, userId, songId);
        } else {
            addFavorite(dataService, userId, songId);
        }
    }

    private void addFavorite(DataService dataService, int userId, int songId) {
        dataService.addFavorite(userId, songId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isFavorite = true;
                    updateFavoriteStatus(true);
                    Toast.makeText(context, "Đã thêm vào yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi thêm yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Add favorite failed: " + t.getMessage());
            }
        });
    }

    private void removeFavorite(DataService dataService, int userId, int songId) {
        dataService.deleteFavorite(userId, songId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    isFavorite = false;
                    updateFavoriteStatus(false);
                    Toast.makeText(context, "Đã xóa khỏi yêu thích", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Lỗi xóa yêu thích", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Remove favorite failed: " + t.getMessage());
            }
        });
    }

    private void updateFavoriteStatus(boolean favorite) {
        isFavorite = favorite;
        if (listener != null) {
            listener.onFavoriteStatusChanged(favorite);
        }
    }


}