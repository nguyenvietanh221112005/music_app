package com.example.music_app.activity;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.music_app.R;
import com.example.music_app.model.UserUpdate;
import com.example.music_app.model.Users;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeAccountActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Email;
    private EditText Password;
    private Button btnConfirmChange;
    private ImageButton Back;

    private UserSessionManager sessionManager;
    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_account);

        // Ánh xạ view
        Name = findViewById(R.id.etName);
        Email = findViewById(R.id.etEmail);
        Password = findViewById(R.id.etPassword);
        btnConfirmChange = findViewById(R.id.btnConfirmChange);
        Back = findViewById(R.id.btnBack);

        sessionManager = new UserSessionManager(this);
        dataService = APIService.getService();

        int userId = sessionManager.getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 👉 Gọi API lấy thông tin người dùng
        loadUserInfo(userId);

        Back.setOnClickListener(v -> finish());

        btnConfirmChange.setOnClickListener(v -> {
            String newName = Name.getText().toString().trim();
            String newEmail = Email.getText().toString().trim();
            String newPassword = Password.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(this, "Hãy điền đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newPassword.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo object UserUpdate để gửi lên server
            UserUpdate userUpdate = new UserUpdate(newName, newEmail, newPassword);
            updateUserInfo(userId, userUpdate);
        });
    }

    // 👉 Hàm gọi API để lấy thông tin người dùng hiện tại
    private void loadUserInfo(int userId) {
        Call<Users> call = dataService.getUserById(userId);
        call.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users user = response.body();
                    // Gán thông tin lên UI
                    Name.setText(user.getTen());
                    Email.setText(user.getEmail());
                    Password.setText(user.getPassword());
                } else {
                    Toast.makeText(ChangeAccountActivity.this, "Không tải được thông tin!", Toast.LENGTH_SHORT).show();
                    Log.e("ChangeAccountActivity", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                Toast.makeText(ChangeAccountActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChangeAccountActivity", "API error: " + t.getMessage());
            }
        });
    }

    // 👉 Hàm gọi API để cập nhật thông tin người dùng
    private void updateUserInfo(int userId, UserUpdate userUpdate) {
        Call<Map<String, Object>> call = dataService.updateUserInfo(userId, userUpdate);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(@NonNull Call<Map<String, Object>> call, @NonNull Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ChangeAccountActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ChangeAccountActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, Object>> call, @NonNull Throwable t) {
                Toast.makeText(ChangeAccountActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}