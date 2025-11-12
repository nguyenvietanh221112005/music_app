package com.example.music_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_app.R;
import com.example.music_app.model.ApiResponse;
import com.example.music_app.model.Users;
import com.example.music_app.service.APIService;
import com.example.music_app.service.DataService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private EditText inputTen, inputEmail, inputPassword;
    private Button buttonRegister;
    private TextView tvLogin ;
    private DataService dataService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputTen = findViewById(R.id.edtUserName);
        inputEmail = findViewById(R.id.edtEmail);
        inputPassword = findViewById(R.id.edtPassword);
        buttonRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvRegister);

        dataService = APIService.getService();

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class) ;
            startActivity(intent);

        });

        buttonRegister.setOnClickListener(v -> {
            String ten = inputTen.getText().toString().trim();
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (validateInput(ten, email, password)) {
                performRegister(ten, email, password);
            }
        });
    }

    private boolean validateInput(String ten, String email, String password) {

        Log.d(TAG, "Email nhập vào: '" + email + "' (length = " + email.length() + ")");
        if (ten.isEmpty()) {
            inputTen.setError("Vui lòng nhập tên");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Email không hợp lệ");
            return false;
        }
        if (password.length() < 6) {
            inputPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        return true;
    }

    private void performRegister(String ten, String email, String password) {
        buttonRegister.setEnabled(false);

        Users user = new Users(ten, email, password);
        String jsonUser = new Gson().toJson(user);
        Log.d(TAG, "JSON gửi đi: " + jsonUser);

        Call<ApiResponse> call = dataService.register(user);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                buttonRegister.setEnabled(true);
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Log.d(TAG, "Response message: " + message);
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (message.contains("Đăng ký thành công")) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Response error: " + response.errorBody().string());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Không đọc được errorBody", e);
                    }
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                buttonRegister.setEnabled(true);
                Log.e(TAG, "Lỗi kết nối Retrofit: ", t);
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}