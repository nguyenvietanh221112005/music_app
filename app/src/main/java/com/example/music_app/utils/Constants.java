package com.example.music_app.utils;

public class Constants {
    // Thay đổi nếu backend bạn dùng khác (10.0.2.2 là localhost trên Android emulator)
    public static final String BASE_URL = "http://172.16.8.98.1:8080/api/";

    // Key SharedPreferences
    public static final String PREF_NAME = "music_app_prefs";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_EMAIL = "user_email";

    // Intents extras
    public static final String EXTRA_THELOAI_ID = "extra_theloai_id";
    public static final String EXTRA_BAIHAT_ID = "extra_baihat_id";
    public static final String EXTRA_BAIHAT_OBJ = "extra_baihat_obj";

    private Constants() { /* Không cho khởi tạo */ }
}
