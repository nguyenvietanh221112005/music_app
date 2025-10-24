package com.example.music_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.music_app.model.ApiResponse;
import com.example.music_app.model.Users;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.example.music_app.utils.UserSessionManager;
import com.example.music_app.MainActivity;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    TextView tvTitle;
    EditText inputEmail, inputPassword;
    Button buttonLogin;
    DataService dataService;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.edtEmail);
        inputPassword = findViewById(R.id.edtPassword);
        buttonLogin = findViewById(R.id.btnLogin);

        dataService = APIService.getService();
        sessionManager = new UserSessionManager(this);

        // Kiểm tra nếu đã đăng nhập
        if (sessionManager.isLoggedIn()) {
            navigateToMain();
            return;
        }

        buttonLogin.setOnClickListener(v -> {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();
            if (validateInput(email, password)) {
                checkLogin(email, password);
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty()) {
            inputEmail.setError("Vui lòng nhập email");
            return false;
        }
        if (password.length() < 6) {
            inputPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void checkLogin(String email, String password) {
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(password);

        Log.d(TAG, "Bắt đầu gọi API đăng nhập...");

        dataService.login(user).enqueue(new Callback<Users>() {
            @Override
            public void onResponse(Call<Users> call, Response<Users> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Users loggedUser = response.body();
                    if (loggedUser.isSuccess()) {
                        Log.d(TAG, "Đăng nhập thành công, user_id: " + loggedUser.getUser_id());

                        // Lưu session
                        sessionManager.createLoginSession(loggedUser.getUser_id(), email);

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                        navigateToMain();
                    } else {
                        Log.e(TAG, "Đăng nhập thất bại");
                        Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Đăng nhập thất bại: " + response.code());
                    Toast.makeText(LoginActivity.this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Users> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(LoginActivity.this, "Không thể kết nối máy chủ: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}