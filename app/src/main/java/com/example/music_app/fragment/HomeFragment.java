package com.example.music_app.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.example.music_app.R;
import com.example.music_app.activity.LikeListActivity;
import com.example.music_app.activity.SearchActivity;
import com.example.music_app.adapter.CategoryAdapter;
import com.example.music_app.adapter.TopSongAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.model.TheLoai;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView recyclerCategory, recyclerTopSongs;
    CategoryAdapter categoryAdapter;
    TopSongAdapter topSongAdapter;
    EditText etSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        recyclerTopSongs = view.findViewById(R.id.recyclerTopSongs);
        etSearch = view.findViewById(R.id.etSearch);

        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTopSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        setupSearch();
        loadTheLoai();
        loadTopBXH();

        return view;
    }

    private void setupSearch() {
        if (etSearch == null) return;

        etSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                etSearch.clearFocus();
            }
        });

        etSearch.setShowSoftInputOnFocus(false);
    }

    private void loadTheLoai() {
        DataService dataService = APIService.getService();
        dataService.getTheLoai().enqueue(new Callback<ArrayList<TheLoai>>() {
            @Override
            public void onResponse(Call<ArrayList<TheLoai>> call, Response<ArrayList<TheLoai>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<TheLoai> list = response.body();
                    Log.d("HomeFragment", "Loaded categories: " + list.size());

                    if (list.isEmpty()) {
                        Log.e("HomeFragment", "Categories list is empty");
                        return;
                    }

                    categoryAdapter = new CategoryAdapter(getContext(), list);
                    recyclerCategory.setAdapter(categoryAdapter);
                } else {
                    Log.e("HomeFragment", "Category API error: " + response.code());
                    Toast.makeText(getContext(), "Lỗi tải thể loại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<TheLoai>> call, Throwable t) {
                Log.e("HomeFragment", "Category API failure: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTopBXH() {
        DataService dataService = APIService.getService();
        dataService.getTopBXH().enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<BaiHat> list = response.body();
                    Log.d("HomeFragment", "Loaded songs: " + list.size());

                    if (list.isEmpty()) {
                        Log.e("HomeFragment", "Songs list is empty");
                        return;
                    }

                    // Kiểm tra context trước khi khởi tạo adapter
                    if (getContext() == null) {
                        Log.e("HomeFragment", "Context is null, cannot create adapter");
                        return;
                    }

                    // Sử dụng requireContext() để đảm bảo context không null
                    topSongAdapter = new TopSongAdapter(requireContext(), list);

//                    // Xử lý sự kiện click bài hát
//                    topSongAdapter.setOnSongClickListener(baiHat -> openMusicPlayer(baiHat));

                    recyclerTopSongs.setAdapter(topSongAdapter);
                } else {
                    Log.e("HomeFragment", "Songs API error: " + response.code());
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Lỗi tải bài hát: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e("HomeFragment", "Songs API failure: " + t.getMessage());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    private void openMusicPlayer(BaiHat baiHat) {
//        Intent intent = new Intent(getActivity(), LikeListActivity.class);
//        intent.putExtra("SONG_ID", baiHat.getId());
//        intent.putExtra("SONG_NAME", baiHat.getTenBaiHat());
//        intent.putExtra("ARTIST", baiHat.getCaSi());
//        intent.putExtra("SONG_URL", baiHat.getLink());
//        intent.putExtra("SONG_IMAGE", baiHat.getHinhAnh());
//        intent.putExtra("SONG_OBJECT", baiHat);
//        startActivity(intent);
//
//        if (getActivity() != null) {
//            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
//        }
//    }
}