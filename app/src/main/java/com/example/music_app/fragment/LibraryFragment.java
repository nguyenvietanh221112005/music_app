package com.example.music_app.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_app.R;
import com.example.music_app.adapter.BaiHatYeuThichAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private RecyclerView recyclerThuVien;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private BaiHatYeuThichAdapter adapter;
    private ArrayList<BaiHat> danhSachYeuThich;
    private DataService dataService;
    private final int userId = 1; // Giả định ID người dùng tạm thời

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        recyclerThuVien = view.findViewById(R.id.recyclerThuVien);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        recyclerThuVien.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerThuVien.setHasFixedSize(true);

        dataService = APIService.getService();
        danhSachYeuThich = new ArrayList<>();

        taiDanhSachYeuThich();

        return view;
    }

    private void taiDanhSachYeuThich() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyState.setVisibility(View.GONE);
        recyclerThuVien.setVisibility(View.GONE);

        Call<ArrayList<BaiHat>> call = dataService.getFavorites(userId);
        call.enqueue(new Callback<ArrayList<BaiHat>>() {
            @Override
            public void onResponse(Call<ArrayList<BaiHat>> call, Response<ArrayList<BaiHat>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    danhSachYeuThich = response.body();
                    if (!danhSachYeuThich.isEmpty()) {
                        adapter = new BaiHatYeuThichAdapter(getContext(), danhSachYeuThich, dataService, userId);
                        recyclerThuVien.setAdapter(adapter);
                        recyclerThuVien.setVisibility(View.VISIBLE);
                        tvEmptyState.setVisibility(View.GONE);
                    } else {
                        hienThongBaoRong("Chưa có bài hát yêu thích 🎧");
                    }
                } else {
                    hienThongBaoRong("Không có dữ liệu hiển thị!");
                    Log.e("API_RESPONSE", "Lỗi response: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ArrayList<BaiHat>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                hienThongBaoRong("Không thể tải dữ liệu!");
                Toast.makeText(getContext(), "Lỗi kết nối server!", Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Lỗi tải danh sách yêu thích", t);
            }
        });
    }

    private void hienThongBaoRong(String message) {
        recyclerThuVien.setVisibility(View.GONE);
        tvEmptyState.setVisibility(View.VISIBLE);
        tvEmptyState.setText(message);
    }
}
