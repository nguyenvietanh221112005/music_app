package com.example.music_app.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.model.TheLoai;
import com.example.music_app.model.BaiHat;
import com.example.music_app.adapter.BaiHatTheLoaiAdapter;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity {

    private ImageView imgBack, imgCategory, imgPlayAll;
    private TextView tvCategoryName, tvSongCount, tvNoSongs;
    private RecyclerView recyclerSongs;

    private TheLoai category;
    private ArrayList<BaiHat> songs;
    private BaiHatTheLoaiAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        initViews();
        getCategoryFromIntent();
        setupListeners();

        if (category != null) {
            loadSongsByCategory(category.getIdTheLoai());
        } else {
            Toast.makeText(this, "Không nhận được thể loại!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgCategory = findViewById(R.id.imgCategory);
        imgPlayAll = findViewById(R.id.imgPlayAll);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvNoSongs = findViewById(R.id.tvNoSongs);
        tvSongCount = findViewById(R.id.tvSongCount);
        recyclerSongs = findViewById(R.id.recyclerSongs);

        recyclerSongs.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();
        songAdapter = new BaiHatTheLoaiAdapter(this, songs, 1);
        recyclerSongs.setAdapter(songAdapter);
    }

    private void getCategoryFromIntent() {
        category = (TheLoai) getIntent().getSerializableExtra("category");
        if (category != null) {
            tvCategoryName.setText(category.getTenTheLoai());
            Picasso.get()
                    .load(category.getHinhAnh())
                    .placeholder(R.drawable.music_placeholder)
                    .error(R.drawable.music_placeholder)
                    .into(imgCategory);
        }
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> finish());
        imgPlayAll.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                Toast.makeText(this, "🎵 Đang phát tất cả bài hát trong thể loại!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không có bài hát nào để phát!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSongsByCategory(int idTheLoai) {
        DataService dataService = APIService.getService();
        Log.d("API_CALL", "Gọi API bài hát theo thể loại id=" + idTheLoai);

        Call<ArrayList<BaiHat>> callback = dataService.getBaiHatByTheLoai(idTheLoai);
        callback.enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    songs.clear();
                    songs.addAll(response.body());
                    songAdapter.notifyDataSetChanged();
                    updateUI();
                    Log.d("API_RESPONSE", "Nhận " + songs.size() + " bài hát");
                } else {
                    showNoSongs();
                    Log.e("API_RESPONSE", "Không có bài hát nào trong thể loại này!");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                showNoSongs();
                Log.e("API_ERROR", "Lỗi tải bài hát: " + t.getMessage());
            }
        });
    }

    private void updateUI() {
        recyclerSongs.setVisibility(RecyclerView.VISIBLE);
        tvNoSongs.setVisibility(TextView.GONE);
        tvSongCount.setText(songs.size() + " bài hát");
    }

    private void showNoSongs() {
        recyclerSongs.setVisibility(RecyclerView.GONE);
        tvNoSongs.setVisibility(TextView.VISIBLE);
        tvSongCount.setText("0 bài hát");
    }
}
