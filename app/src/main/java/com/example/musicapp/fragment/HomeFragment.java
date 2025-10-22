package com.example.musicapp.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapp.R;
import com.example.musicapp.adapter.CategoryAdapter;
import com.example.musicapp.adapter.TopSongAdapter;
import com.example.musicapp.model.BaiHat;
import com.example.musicapp.model.TheLoai;
import com.example.musicapp.service.APIService;
import com.example.musicapp.service.DataService;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    RecyclerView recyclerCategory, recyclerTopSongs;
    CategoryAdapter categoryAdapter;
    TopSongAdapter topSongAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerCategory = view.findViewById(R.id.recyclerCategory);
        recyclerTopSongs = view.findViewById(R.id.recyclerTopSongs);

        // Khởi tạo layout manager trước
        recyclerCategory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerTopSongs.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTheLoai();
        loadTopBXH();

        return view;
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
                t.printStackTrace();
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

                    topSongAdapter = new TopSongAdapter(getContext(), list);
                    recyclerTopSongs.setAdapter(topSongAdapter);
                } else {
                    Log.e("HomeFragment", "Songs API error: " + response.code());
                    Toast.makeText(getContext(), "Lỗi tải bài hát: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                Log.e("HomeFragment", "Songs API failure: " + t.getMessage());
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}
