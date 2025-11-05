package com.example.music_app.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.music_app.activity.RegisterActivity;
import com.example.music_app.utils.UserSessionManager;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.music_app.R;
import com.example.music_app.activity.LoginActivity;
import com.example.music_app.model.Users;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountFragment extends Fragment {

    private TextView tvUserName, tvUserEmail;
    private Button btnLogout;

    private UserSessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tvUserName = view.findViewById(R.id.tvUserName);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        btnLogout = view.findViewById(R.id.btnLogout);

        //  Khởi tạo sessionManager , khi context đã sẵn sàng
        sessionManager = new UserSessionManager(requireContext());

        int userId = sessionManager.getUserId();
        Log.d("AccountFragment", "User ID hiện tại: " + userId);

        if (userId != -1) {
            loadUserInfo(userId);
        } else {
            Log.d("AccountFragment", "Chưa có user_id, sẽ hiển thị 'Chưa đăng nhập'");
            tvUserName.setText("Chưa đăng nhập");
            tvUserEmail.setText("");
        }

        //🔹 Sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> {
            sessionManager.logoutUser(); // Xóa user_id đã lưu

            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });


        return view;
    }

    // 🔹 Hàm lấy thông tin người dùng qua API
    private void loadUserInfo(int userId) {
        DataService dataService = APIService.getService();
        if (dataService == null) {
            Log.e("AccountFragment", "❌ DataService null!");
            tvUserName.setText("Không thể kết nối server");
            return;
        }

        Call<Users> call = dataService.getUserById(userId);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    tvUserName.setText(user.getTen());
                    tvUserEmail.setText(user.getEmail());
                } else {
                    tvUserName.setText("Không tải được thông tin");
                    tvUserEmail.setText("");
                    Log.e("AccountFragment", "⚠️ Response lỗi: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                tvUserName.setText("Lỗi kết nối");
                tvUserEmail.setText("Không thể tải thông tin người dùng");
                Log.e("AccountFragment", "❌ Lỗi API: " + t.getMessage());
            }
        });
    }
}