package com.example.music_app.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_app.R;
import com.example.music_app.adapter.FavoriteManager;
import com.example.music_app.adapter.MediaPlayerManager;
import com.example.music_app.adapter.MusicPlayerUIController;
import com.example.music_app.adapter.PlaylistManager;
import com.example.music_app.model.BaiHat;

import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayerActivity";

    // UI Components
    private ImageView imgBack, imgFavorite, imgSongImage;
    private ImageView imgPrevious, imgPlayPause, imgNext;
    private TextView tvSongName, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    // Data
    private BaiHat currentSong;

    // Managers
    private MediaPlayerManager playerManager;
    private MusicPlayerUIController uiController;
    private FavoriteManager favoriteManager;
    private PlaylistManager playlistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initViews();
        initManagers();
        getSongFromIntent();
        setupListeners();
    }

    private void initViews() {
        imgBack = findViewById(R.id.imgBack);
        imgFavorite = findViewById(R.id.imgFavorite);
        imgSongImage = findViewById(R.id.imgSongImage);
        imgPrevious = findViewById(R.id.imgPrevious);
        imgPlayPause = findViewById(R.id.imgPlayPause);
        imgNext = findViewById(R.id.imgNext);
        tvSongName = findViewById(R.id.tvSongName);
        tvArtist = findViewById(R.id.tvArtist);
        tvCurrentTime = findViewById(R.id.tvCurrentTime);
        tvTotalTime = findViewById(R.id.tvTotalTime);
        seekBar = findViewById(R.id.seekBar);

        Log.d(TAG, "✅ Views initialized");
    }

    private void initManagers() {
        // Playlist Manager (khởi tạo trước)
        playlistManager = new PlaylistManager(new PlaylistManager.PlaylistListener() {
            @Override
            public void onSongChanged(BaiHat song, int position) {
                if (song != null) {
                    currentSong = song;
                    uiController.displaySongInfo(song);
                    playerManager.initializeSong(song);
                    favoriteManager.checkFavoriteStatus(song);
                    Log.d(TAG, "🎵 Changed to: " + song.getTenBaiHat());
                }
            }

            @Override
            public void onPlaylistEmpty() {
                Toast.makeText(MusicPlayerActivity.this, "Danh sách trống", Toast.LENGTH_SHORT).show();
            }
        });

        // Media Player Manager
        playerManager = new MediaPlayerManager(this, new MediaPlayerManager.MediaPlayerListener() {
            @Override
            public void onPrepared(int duration) {
                Log.d(TAG, "✅ Song prepared, duration: " + duration + "ms");
                uiController.setTotalTime(duration);

                // Tự động phát sau khi prepared
                playerManager.play();
                uiController.updatePlayPauseIcon(true);
                uiController.startRotation();
                uiController.startSeekBarUpdate(playerManager);
            }

            @Override
            public void onCompletion() {
                Log.d(TAG, "Song completed");
                uiController.updatePlayPauseIcon(false);
                uiController.stopRotation();
                uiController.stopSeekBarUpdate();

                // Tự động chuyển bài tiếp theo
                BaiHat nextSong = playlistManager.getNextSong();
                if (nextSong == null) {
                    Toast.makeText(MusicPlayerActivity.this, "Đã hết danh sách phát", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String message) {
                Log.e(TAG, "Player error: " + message);
                Toast.makeText(MusicPlayerActivity.this, message, Toast.LENGTH_LONG).show();
                uiController.updatePlayPauseIcon(false);
                uiController.stopRotation();
                uiController.stopSeekBarUpdate();
            }
        });

        // UI Controller
        uiController = new MusicPlayerUIController(this);
        uiController.bindViews(imgSongImage, imgPlayPause, imgFavorite,
                tvSongName, tvArtist, tvCurrentTime, tvTotalTime, seekBar);

        // Favorite Manager
        favoriteManager = new FavoriteManager(this, isFavorite -> {
            uiController.updateFavoriteIcon(isFavorite);
        });

        Log.d(TAG, "✅ Managers initialized");
    }

    private void getSongFromIntent() {
        try {
            // Lấy bài hát từ SONG_OBJECT
            currentSong = (BaiHat) getIntent().getSerializableExtra("SONG_OBJECT");

            // Lấy danh sách bài hát (nếu có)
            ArrayList<BaiHat> playlist = (ArrayList<BaiHat>) getIntent().getSerializableExtra("PLAYLIST");
            int position = getIntent().getIntExtra("POSITION", 0);

            // Fallback: Tạo từ extras riêng lẻ nếu SONG_OBJECT null
            if (currentSong == null) {
                Log.d(TAG, "SONG_OBJECT is null, creating from individual extras");

                int songId = getIntent().getIntExtra("SONG_ID", -1);
                String songName = getIntent().getStringExtra("SONG_NAME");
                String artist = getIntent().getStringExtra("ARTIST");
                String songUrl = getIntent().getStringExtra("SONG_URL");
                String songImage = getIntent().getStringExtra("SONG_IMAGE");

                if (songId == -1 || songName == null || songUrl == null) {
                    Toast.makeText(this, "Không tải được thông tin bài hát", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Toast.makeText(this, "Lỗi: Dữ liệu bài hát không hợp lệ", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Validate song
            if (currentSong.getLink() == null || currentSong.getLink().isEmpty()) {
                Toast.makeText(this, "Bài hát không có link", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "❌ Song has no URL: " + currentSong.getTenBaiHat());
                finish();
                return;
            }

            if (!currentSong.getLink().startsWith("http://") && !currentSong.getLink().startsWith("https://")) {
                Toast.makeText(this, "URL không hợp lệ", Toast.LENGTH_LONG).show();
                Log.e(TAG, "❌ Invalid URL format: " + currentSong.getLink());
                finish();
                return;
            }

            // Khởi tạo playlist
            if (playlist != null && !playlist.isEmpty()) {
                playlistManager.setPlaylist(playlist, position);
                Log.d(TAG, "✅ Playlist loaded: " + playlist.size() + " songs, position: " + position);
            } else {
                // Nếu không có playlist, tạo playlist chỉ có bài hiện tại
                ArrayList<BaiHat> singleSongList = new ArrayList<>();
                singleSongList.add(currentSong);
                playlistManager.setPlaylist(singleSongList, 0);
                Log.d(TAG, "⚠️ No playlist, created single-song playlist");
            }

            // Hiển thị thông tin bài hát
            uiController.displaySongInfo(currentSong);

            // Log thông tin debug
            Log.d(TAG, "✅ Song loaded: " + currentSong.getTenBaiHat());
            Log.d(TAG, "📍 Song URL: " + currentSong.getLink());

            // Khởi tạo MediaPlayer
            playerManager.initializeSong(currentSong);

            // Kiểm tra trạng thái yêu thích
            favoriteManager.checkFavoriteStatus(currentSong);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error getting song: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Lỗi tải bài hát: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        // Nút quay lại
        imgBack.setOnClickListener(v -> {
            finish();
        });

        // Nút yêu thích
        imgFavorite.setOnClickListener(v -> {
            favoriteManager.toggleFavorite(currentSong);
        });

        // Nút play/pause
        imgPlayPause.setOnClickListener(v -> {
            Log.d(TAG, "Play/Pause clicked, current state: isPlaying=" + playerManager.isPlaying());

            if (playerManager.isPlaying()) {
                // Đang phát -> Tạm dừng
                playerManager.pause();
                uiController.updatePlayPauseIcon(false);
                uiController.stopRotation();
                uiController.stopSeekBarUpdate();
                Log.d(TAG, "⏸️ Music paused");
            } else {
                // Đang dừng -> Phát
                playerManager.play();
                uiController.updatePlayPauseIcon(true);
                uiController.startRotation();
                uiController.startSeekBarUpdate(playerManager);
                Log.d(TAG, "▶️ Music playing");
            }
        });

        // Nút previous
        imgPrevious.setOnClickListener(v -> {
            if (playerManager.getCurrentPosition() > 3000) {
                // Nếu đã phát hơn 3 giây, về đầu bài
                playerManager.seekTo(0);
                Log.d(TAG, "⏮️ Restart song");
            } else {
                // Nếu chưa phát quá 3 giây, chuyển bài trước
                BaiHat previousSong = playlistManager.getPreviousSong();
                if (previousSong == null) {
                    Toast.makeText(this, "Đây là bài đầu tiên", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Nút next
        imgNext.setOnClickListener(v -> {
            BaiHat nextSong = playlistManager.getNextSong();
            if (nextSong == null) {
                Toast.makeText(this, "Không có bài hát tiếp theo", Toast.LENGTH_SHORT).show();
            }
        });

        // SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && playerManager != null && playerManager.isPrepared()) {
                    playerManager.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiController.stopSeekBarUpdate();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (playerManager.isPlaying()) {
                    uiController.startSeekBarUpdate(playerManager);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (playerManager != null) {
            playerManager.release();
        }
        if (uiController != null) {
            uiController.cleanup();
        }
        Log.d(TAG, "🔚 Activity destroyed");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity trở lại
    }
}