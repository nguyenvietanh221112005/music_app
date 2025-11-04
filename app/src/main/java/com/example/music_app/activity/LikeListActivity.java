package com.example.music_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.adapter.BaiHatYeuThichAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LikeListActivity extends AppCompatActivity implements BaiHatYeuThichAdapter.OnSongClickListener {

    private RecyclerView recyclerLiked;
    private BaiHatYeuThichAdapter adapter;
    private ArrayList<BaiHat> likedSongs;
    private DataService dataService;
    private UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_library);

        recyclerLiked = findViewById(R.id.recyclerThuVien);
        recyclerLiked.setLayoutManager(new LinearLayoutManager(this));

        dataService = APIService.getService();
        sessionManager = new UserSessionManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLikedSongs();
    }

    private void loadLikedSongs() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem yêu thích", Toast.LENGTH_SHORT).show();
            return;
        }

        int userId = sessionManager.getUserId();
        dataService.getFavorites(userId).enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    likedSongs = response.body();
                    adapter = new BaiHatYeuThichAdapter(LikeListActivity.this, likedSongs, dataService, userId, LikeListActivity.this);
                    recyclerLiked.setAdapter(adapter);
                    Log.d("LikeListActivity", "Loaded " + likedSongs.size() + " favorites");
                } else {
                    Toast.makeText(LikeListActivity.this, "Không có bài hát yêu thích nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Toast.makeText(LikeListActivity.this, "Lỗi tải dữ liệu!", Toast.LENGTH_SHORT).show();
                Log.e("LikeListActivity", "API error: " + t.getMessage());
            }
        });
    }

    @Override
    public void onSongClick(BaiHat song, int position) {
        // Mở MusicPlayerActivity (nhớ khai báo trong Manifest)
        Intent intent = new Intent(this, test.class); // đổi tên class nếu khác
        intent.putExtra("SONG_OBJECT", song);
        intent.putExtra("PLAYLIST", likedSongs);
        intent.putExtra("POSITION", position);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }
}
