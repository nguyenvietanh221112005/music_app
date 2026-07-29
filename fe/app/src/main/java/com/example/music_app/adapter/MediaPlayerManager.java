package com.example.music_app.adapter;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.example.music_app.model.BaiHat;

import java.io.IOException;

public class MediaPlayerManager {
    private static final String TAG = "MediaPlayerManager";

    private MediaPlayer mediaPlayer;
    private final Context context;
    private final MediaPlayerListener listener;
    private boolean isPrepared = false;

    public interface MediaPlayerListener {
        void onPrepared(int duration);
        void onCompletion();
        void onError(String message);
    }

    public MediaPlayerManager(Context context, MediaPlayerListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public boolean isPrepared() {
        return isPrepared;
    }

    public void initializeSong(BaiHat song) {
        if (song == null || song.getLink() == null || song.getLink().isEmpty()) {
            if (listener != null) listener.onError("Không có link bài hát");
            return;
        }

        release();
        isPrepared = false;

        try {
            mediaPlayer = new MediaPlayer();

            // ✅ Dùng AudioAttributes thay cho setAudioStreamType (Android 10+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build()
                );
            } else {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            Log.d(TAG, "🔗 Setting data source: " + song.getLink());
            mediaPlayer.setDataSource(song.getLink());

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d(TAG, "✅ MediaPlayer prepared");
                isPrepared = true;
                if (listener != null) listener.onPrepared(mp.getDuration());
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                if (listener != null) listener.onCompletion();
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e(TAG, "❌ MediaPlayer error: what=" + what + ", extra=" + extra);
                isPrepared = false;
                if (listener != null) listener.onError(getErrorMessage(what, extra));
                return true;
            });

            mediaPlayer.prepareAsync();
            Log.d(TAG, "⏳ Preparing media player...");

        } catch (IOException | IllegalArgumentException e) {
            Log.e(TAG, "❌ Error setting up MediaPlayer: " + e.getMessage());
            if (listener != null) listener.onError("Không thể phát bài hát này");
        }
    }

    public void play() {
        if (mediaPlayer != null && isPrepared) {
            try {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    Log.d(TAG, "▶️ Playing music");
                }
            } catch (IllegalStateException e) {
                if (listener != null) listener.onError("Lỗi phát nhạc");
            }
        }
    }

    public void pause() {
        if (mediaPlayer != null && isPrepared && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            Log.d(TAG, "⏸️ Music paused");
        }
    }

    public void seekTo(int position) {
        if (mediaPlayer != null && isPrepared) {
            mediaPlayer.seekTo(position);
        }
    }

    public boolean isPlaying() {
        try {
            return mediaPlayer != null && isPrepared && mediaPlayer.isPlaying();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public int getCurrentPosition() {
        return (mediaPlayer != null && isPrepared) ? mediaPlayer.getCurrentPosition() : 0;
    }


    public void release() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (IllegalStateException ignored) {
            } finally {
                mediaPlayer = null;
                isPrepared = false;
            }
        }
    }

    private String getErrorMessage(int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                return "Lỗi máy chủ media";
            case MediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                switch (extra) {
                    case MediaPlayer.MEDIA_ERROR_IO:
                        return "Lỗi đọc file (kiểm tra mạng)";
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                        return "File media không hợp lệ";
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                        return "Định dạng không được hỗ trợ";
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                        return "Hết thời gian chờ";
                    default:
                        return "Lỗi phát nhạc";
                }
        }
    }
}