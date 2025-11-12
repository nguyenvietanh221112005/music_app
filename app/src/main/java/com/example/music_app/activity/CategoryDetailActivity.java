package com.example.music_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.adapter.BaiHatTheLoaiAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.model.TheLoai;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//là một màn hình hiển thị chi tiết một thể loại và danh sách bài hát của thể loại đó
public class CategoryDetailActivity extends AppCompatActivity implements BaiHatTheLoaiAdapter.OnSongClickListener {
                                                                //màn hình này sẽ cài đặt interface của adapter để nhận call back
                                                                //khi user click vào một bài hát trong danh sách
    private ImageView imgBack, imgCategory, imgPlayAll;
    private TextView tvCategoryName, tvSongCount, tvNoSongs;
    private RecyclerView recyclerSongs;

    private TheLoai category;
    private ArrayList<BaiHat> songs;
    private BaiHatTheLoaiAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);//gán layout XML activity_category_detail cho Activity (tải giao diện).

        initViews();
        getCategoryFromIntent();
        setupListeners();

        if (category != null) {//nếu nhận được thể loại hợp lệ
            loadSongsByCategory(category.getIdTheLoai());//gọi hàm loadSongsByCategory hiện thị danh sách bài hát theo chủ đề
        } else {
            Toast.makeText(this, "Không nhận được thể loại!", Toast.LENGTH_SHORT).show();//không thì hiện thông báo
            finish();
        }
    }

    private void initViews() {
        //ánh xạ đến các view tương ứng từ layout để thao tác
        imgBack = findViewById(R.id.imgBack);
        imgCategory = findViewById(R.id.imgCategory);
        imgPlayAll = findViewById(R.id.imgPlayAll);
        tvCategoryName = findViewById(R.id.tvCategoryName);
        tvNoSongs = findViewById(R.id.tvNoSongs);
        tvSongCount = findViewById(R.id.tvSongCount);
        recyclerSongs = findViewById(R.id.recyclerSongs);
        //đặt LinearLayoutManager khiến RecyclerView hiển thị theo cột dọc (list).
        recyclerSongs.setLayoutManager(new LinearLayoutManager(this));
        songs = new ArrayList<>();//khởi tạo một danh sách trống lưu các bài hát khi api trả về
        songAdapter = new BaiHatTheLoaiAdapter(this, songs, this); //khởi tạo adapter
        recyclerSongs.setAdapter(songAdapter);//gắn adapter vào RecyclerView để hiện thị
    }

    private void getCategoryFromIntent() {//lấy dữ liệu thể loại từ activity trước đó thông qua Intent
        category = (TheLoai) getIntent().getSerializableExtra("category");
        if (category != null) {
            tvCategoryName.setText(category.getTenTheLoai());// hiển thị tên thể loại
            Picasso.get()
                    .load(category.getHinhAnh())
                    .placeholder(R.drawable.music_placeholder)
                    .error(R.drawable.music_placeholder)
                    .into(imgCategory);
        }
    }


    //gắn hành vi cho các nút
    private void setupListeners() {
        imgBack.setOnClickListener(v -> finish());
        imgPlayAll.setOnClickListener(v -> {
            if (!songs.isEmpty()) {
                Toast.makeText(this, "🎵 Đang phát tất cả bài hát trong thể loại!", Toast.LENGTH_SHORT).show();
                openMusicPlayer(songs.get(0), 0);
            } else {
                Toast.makeText(this, "Không có bài hát nào để phát!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSongsByCategory(int idTheLoai) {
        DataService dataService = APIService.getService();//gọi API lấy bài theo thể loại
        Log.d("API_CALL", "Gọi API bài hát theo thể loại id=" + idTheLoai);

        Call<ArrayList<BaiHat>> callback = dataService.getBaiHatByTheLoai(idTheLoai);
        callback.enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    songs.clear();//xóa dữ liệu cũ.
                    songs.addAll(response.body());//thêm bài hát mới vào danh sách
                    songAdapter.notifyDataSetChanged();//thông báo adapter cập nhật giao diện
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

    // Mở MusicPlayer
    private void openMusicPlayer(BaiHat baiHat, int position) {
        Intent intent = new Intent(this, MusicPlayerActivity.class);
        intent.putExtra("SONG_OBJECT", baiHat);
        intent.putExtra("PLAYLIST", new ArrayList<>(songs));
        intent.putExtra("POSITION", position);
        Log.d("Activity_CategoryDetail", "🎵 Gửi playlist: " + songs.size() + " bài hát, vị trí: " + position);

        startActivity(intent);//bắt đầu activity mới
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
    }

    // Callback từ Adapter
    @Override
    public void onSongClick(BaiHat song, int position) {
        openMusicPlayer(song, position);
    }

}