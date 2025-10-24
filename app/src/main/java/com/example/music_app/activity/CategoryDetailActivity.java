package com.example.music_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.model.TheLoai;
import com.example.music_app.model.BaiHat;
import com.example.music_app.adapter.TopSongAdapter;
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
    private TopSongAdapter songAdapter;

    private TheLoai category;
    private ArrayList<BaiHat> songs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        initViews();
        getCategoryFromIntent();
        setupListeners();
        loadSongsByCategory();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgCategory = findViewById(R.id.imgCategory);
        imgPlayAll = findViewById(R.id.imgPlayAll);
        tvCategoryName = findViewById(R.id.tvCategoryName);

        tvNoSongs = findViewById(R.id.tvNoSongs);
        recyclerSongs = findViewById(R.id.recyclerSongs);

        recyclerSongs.setLayoutManager(new LinearLayoutManager(this));

        songAdapter = new TopSongAdapter(this, songs);
        recyclerSongs.setAdapter(songAdapter);
    }

    private void getCategoryFromIntent() {
        category = (TheLoai) getIntent().getSerializableExtra("category");
        if (category != null) {
            tvCategoryName.setText(category.getTenTheLoai());
            Picasso.get().load(category.getHinhAnh()).into(imgCategory);
        }
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> finish());

        imgPlayAll.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                // Phát bài hát đầu tiên trong danh sách
                openMusicPlayer(songs.get(0));
            }
        });
    }

    private void loadSongsByCategory() {
        if (category == null) return;

        DataService dataService = APIService.getService();
        // Gọi API lấy bài hát theo thể loại
        dataService.getBaiHatByTheLoai(category.getIdTheLoai()).enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    songs.clear();
                    songs.addAll(response.body());
                    updateUI();
                    setupSongClickListener();
                } else {
                    showNoSongs();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                showNoSongs();
            }
        });
    }

    private void setupSongClickListener() {
        songAdapter.setOnSongClickListener(this::openMusicPlayer);
    }

    private void openMusicPlayer(BaiHat baiHat) {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra("SONG_ID", baiHat.getId());
        intent.putExtra("SONG_NAME", baiHat.getTenBaiHat());
        intent.putExtra("ARTIST", baiHat.getCaSi());
        intent.putExtra("SONG_URL", baiHat.getLink());
        intent.putExtra("SONG_IMAGE", baiHat.getHinhAnh());
        intent.putExtra("SONG_OBJECT", baiHat);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    private void updateUI() {
        if (songs.isEmpty()) {
            showNoSongs();
        } else {
            showSongsList();
        }

        songAdapter.notifyDataSetChanged();
    }

    private void showSongsList() {
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