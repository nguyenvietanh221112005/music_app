package com.example.music_app.fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.content.Intent;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_app.MainActivity;
import com.example.music_app.R;
import com.example.music_app.activity.MusicPlayerActivity;
import com.example.music_app.adapter.SearchMusicAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Tim_Kiem  extends Fragment{
    private EditText etSearch;
    private ImageButton btnBack ;
    private RecyclerView recyclerSearchResults;

    private SearchMusicAdapter searchAdapter;
    private List<BaiHat> baiHatList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Ánh xạ view
        etSearch = view.findViewById(R.id.etSearch);
        recyclerSearchResults = view.findViewById(R.id.recyclerSearchResults);

        btnBack = view.findViewById(R.id.btnBack);

        // Thiết lập RecyclerView
        searchAdapter = new SearchMusicAdapter(getContext(), baiHatList);
        recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSearchResults.setAdapter(searchAdapter);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        searchAdapter.setOnItemClickListener((baiHat, position) -> {
            openMusicPlayer(baiHat, position);
        });

        // Thêm sự kiện lắng nghe khi người dùng nhập từ khóa
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    searchSongs(keyword);

                    recyclerSearchResults.setVisibility(View.VISIBLE);
                } else {
                    recyclerSearchResults.setVisibility(View.GONE);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void searchSongs(String keyword) {
        DataService dataService = APIService.getService();
        Log.d("API_RESPONSE", "Bắt đầu gọi API với keyword: " + keyword);

        Call<List<BaiHat>> callback = dataService.searchSongs(keyword);

        callback.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                Log.d("API_RESPONSE", "Phản hồi từ server, code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    baiHatList = response.body();
                    Log.d("API_RESPONSE", "Dữ liệu bài hát nhận được: " + new com.google.gson.Gson().toJson(baiHatList));

                    searchAdapter.setData(baiHatList);
                } else {
                    Log.e("API_RESPONSE", "Lỗi response! Body null hoặc thất bại. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<BaiHat>> call, Throwable t) {
                Log.e("API_RESPONSE", "Gọi API thất bại: " + t.getMessage(), t);
            }
        });
    }

    private void openMusicPlayer(BaiHat baiHat, int position) {
        Intent intent = new Intent(getActivity(),MusicPlayerActivity.class);
        intent.putExtra("SONG_OBJECT", baiHat);

        if (baiHatList != null && !baiHatList.isEmpty()) {
            intent.putExtra("PLAYLIST", new ArrayList<>(baiHatList));
            intent.putExtra("POSITION", position);
            Log.d("Fragment_Tim_Kiem", "🎵 Gửi playlist: " + baiHatList.size() + " bài hát, vị trí: " + position);
        } else {
            Log.w("Fragment_Tim_Kiem", "⚠️ Danh sách rỗng!");
        }

        startActivity(intent);
        if (getActivity() != null) {
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        }
    }



}