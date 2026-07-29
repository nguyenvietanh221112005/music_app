package com.example.music_app.fragment;

import android.content.Intent;
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
import com.example.music_app.activity.MusicPlayerActivity;
import com.example.music_app.adapter.BaiHatYeuThichAdapter;
import com.example.music_app.model.BaiHat;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;


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
    private UserSessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);//inflate để chuyển đổi một tệp xml thành view để android có thể thao tác được

        recyclerThuVien = view.findViewById(R.id.recyclerThuVien);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        recyclerThuVien.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerThuVien.setHasFixedSize(true);//tối ưu hiệu suất cho RecyclerView nó cho biết rằng kích thước tổng thể của RecyclerView sẽ không đổi dù dữ liệu bên trong có thay đổi ,Khi set true, Android không cần tính toán lại kích thước mỗi lần cập nhật, giúp cuộn mượt hơn và đỡ lag.

        dataService = APIService.getService();
        sessionManager = new UserSessionManager(getContext());
        danhSachYeuThich = new ArrayList<>();
        taiDanhSachYeuThich();

        return view;
    }

    private void taiDanhSachYeuThich() {
        if (!sessionManager.isLoggedIn()) {
            hienThongBaoRong("Vui lòng đăng nhập để xem bài hát yêu thích!");
            return;
        }

        int userId = sessionManager.getUserId();// nếu đăng nhập rồi Lấy ID người dùng hiện tại

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
                    //Nếu danh sách có dữ liệu:
                    if (!danhSachYeuThich.isEmpty()) {
                        adapter = new BaiHatYeuThichAdapter(//Adapter được khởi tạo
                                getContext(),//
                                danhSachYeuThich,
                                dataService,
                                userId,
                                (baiHat, position) -> openMusicPlayer(baiHat, position)
                        );
                        recyclerThuVien.setAdapter(adapter);//Gắn adapter cho RecyclerView → Hiển thị dữ liệu lên giao diện.
                        recyclerThuVien.setVisibility(View.VISIBLE);// hiện thị danh sách
                        tvEmptyState.setVisibility(View.GONE);
                    } else {
                        hienThongBaoRong("Chưa có bài hát yêu thích 🎧");
                    }
                } else {    // Nếu danh sách rỗng:
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

    private void openMusicPlayer(BaiHat baiHat, int position) {
        Intent intent = new Intent(getContext(), MusicPlayerActivity.class);
        //putExtra() dùng để truyền dữ liệu giữa các màn hình
        intent.putExtra("SONG_OBJECT", baiHat);//Gửi đối tượng bài hát mà người dùng chọn sang MusicPlayerActivity.
        intent.putExtra("PLAYLIST", danhSachYeuThich);//Gửi toàn bộ danh sách bài hát yêu thích sang MusicPlayerActivity.
        intent.putExtra("POSITION", position);//Gửi vị trí (index) của bài hát được chọn trong danh sách.
        startActivity(intent);//Thực hiện chuyển sang MusicPlayerActivity với dữ liệu vừa gửi đi.
    }
}