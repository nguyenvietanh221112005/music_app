package com.example.music_app.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_app.R;
import com.example.music_app.adapter.TopSongAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.model.TheLoai;
import com.example.music_app.adapter.CategoryAdapter;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView imgBack, imgClear;
    private TextView tvRecentSearches, tvNoResults;
    private RecyclerView recyclerSearchResults, recyclerCategories, recyclerRecentSongs;
    private TopSongAdapter songAdapter;
    private CategoryAdapter categoryAdapter;
    private TopSongAdapter recentSongAdapter;

    private ArrayList<BaiHat> allSongs = new ArrayList<>();
    private ArrayList<TheLoai> allCategories = new ArrayList<>();
    private ArrayList<BaiHat> searchResults = new ArrayList<>();
    private ArrayList<BaiHat> recentSongs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupListeners();
        loadAllData();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        imgBack = findViewById(R.id.imgBack);
        imgClear = findViewById(R.id.imgClear);
        tvRecentSearches = findViewById(R.id.tvRecentSearches);
        tvNoResults = findViewById(R.id.tvNoResults);

        recyclerSearchResults = findViewById(R.id.recyclerSearchResults);
        recyclerCategories = findViewById(R.id.recyclerCategories);
        recyclerRecentSongs = findViewById(R.id.recyclerRecentSongs);

        // Setup layout managers
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(this));
        recyclerCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerRecentSongs.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapters
        songAdapter = new TopSongAdapter(this, searchResults);
        categoryAdapter = new CategoryAdapter(this, allCategories);
        recentSongAdapter = new TopSongAdapter(this, recentSongs);

        recyclerSearchResults.setAdapter(songAdapter);
        recyclerCategories.setAdapter(categoryAdapter);
        recyclerRecentSongs.setAdapter(recentSongAdapter);

        // Focus vào ô tìm kiếm khi mở activity
        etSearch.requestFocus();

        // Ẩn kết quả tìm kiếm ban đầu, hiển thị danh sách gợi ý
        showSuggestions();
    }

    private void setupListeners() {
        imgBack.setOnClickListener(v -> finish());

        imgClear.setOnClickListener(v -> {
            etSearch.setText("");
            showSuggestions();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    imgClear.setVisibility(ImageView.VISIBLE);
                    performSearch(s.toString());
                } else {
                    imgClear.setVisibility(ImageView.GONE);
                    showSuggestions();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllData() {
        loadAllSongs();
        loadAllCategories();
        loadRecentSongs();
    }

    private void loadAllSongs() {
        DataService dataService = APIService.getService();
        dataService.getTopBXH().enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allSongs.clear();
                    allSongs.addAll(response.body());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    private void loadAllCategories() {
        DataService dataService = APIService.getService();
        dataService.getTheLoai().enqueue(new Callback<ArrayList<TheLoai>>() {
            @Override
            public void onResponse(Call<ArrayList<TheLoai>> call, Response<ArrayList<TheLoai>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allCategories.clear();
                    allCategories.addAll(response.body());
                    categoryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TheLoai>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }

    private void loadRecentSongs() {
        // TODO: Load danh sách bài hát gần đây từ SharedPreferences hoặc database
        // Tạm thời lấy 5 bài hát đầu tiên từ allSongs
        if (allSongs.size() > 0) {
            recentSongs.clear();
            int count = Math.min(5, allSongs.size());
            for (int i = 0; i < count; i++) {
                recentSongs.add(allSongs.get(i));
            }
            recentSongAdapter.notifyDataSetChanged();
        }
    }

    private void performSearch(String query) {
        searchResults.clear();

        // Tìm kiếm trong bài hát
        for (BaiHat song : allSongs) {
            if (song.getTenBaiHat().toLowerCase().contains(query.toLowerCase()) ||
                    song.getCaSi().toLowerCase().contains(query.toLowerCase())) {
                searchResults.add(song);
            }
        }

        // Tìm kiếm trong thể loại
        ArrayList<TheLoai> categoryResults = new ArrayList<>();
        for (TheLoai category : allCategories) {
            if (category.getTenTheLoai().toLowerCase().contains(query.toLowerCase())) {
                categoryResults.add(category);
            }
        }

        if (searchResults.isEmpty() && categoryResults.isEmpty()) {
            showNoResults();
        } else {
            showSearchResults(searchResults, categoryResults);
        }
    }

    private void showSuggestions() {
        recyclerSearchResults.setVisibility(RecyclerView.GONE);
        recyclerCategories.setVisibility(RecyclerView.VISIBLE);
        recyclerRecentSongs.setVisibility(RecyclerView.VISIBLE);
        tvRecentSearches.setVisibility(TextView.VISIBLE);
        tvNoResults.setVisibility(TextView.GONE);
    }

    private void showSearchResults(ArrayList<BaiHat> songs, ArrayList<TheLoai> categories) {
        recyclerSearchResults.setVisibility(RecyclerView.VISIBLE);
        recyclerCategories.setVisibility(RecyclerView.GONE);
        recyclerRecentSongs.setVisibility(RecyclerView.GONE);
        tvRecentSearches.setVisibility(TextView.GONE);
        tvNoResults.setVisibility(TextView.GONE);

        songAdapter.notifyDataSetChanged();

        // Cập nhật categories cho kết quả tìm kiếm
        allCategories.clear();
        allCategories.addAll(categories);
        categoryAdapter.notifyDataSetChanged();
    }

    private void showNoResults() {
        recyclerSearchResults.setVisibility(RecyclerView.GONE);
        recyclerCategories.setVisibility(RecyclerView.GONE);
        recyclerRecentSongs.setVisibility(RecyclerView.GONE);
        tvRecentSearches.setVisibility(TextView.GONE);
        tvNoResults.setVisibility(TextView.VISIBLE);
    }
}