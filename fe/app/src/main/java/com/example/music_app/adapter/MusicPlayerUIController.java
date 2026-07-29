package com.example.music_app.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.music_app.R;
import com.example.music_app.model.BaiHat;

public class MusicPlayerUIController {

    private Context context;
    private ImageView imgSongImage, imgPlayPause, imgFavorite;
    private TextView tvSongName, tvArtist, tvCurrentTime, tvTotalTime;
    private SeekBar seekBar;

    private RotateAnimation rotateAnimation;
    private boolean isRotating = false;
    private Handler handler = new Handler();
    private Runnable seekBarUpdater;

    public MusicPlayerUIController(Context context) {
        this.context = context;
    }

    public void bindViews(ImageView imgSongImage, ImageView imgPlayPause, ImageView imgFavorite,
                          TextView tvSongName, TextView tvArtist, TextView tvCurrentTime,
                          TextView tvTotalTime, SeekBar seekBar) {
        this.imgSongImage = imgSongImage;
        this.imgPlayPause = imgPlayPause;
        this.imgFavorite = imgFavorite;
        this.tvSongName = tvSongName;
        this.tvArtist = tvArtist;
        this.tvCurrentTime = tvCurrentTime;
        this.tvTotalTime = tvTotalTime;
        this.seekBar = seekBar;
    }

    public void displaySongInfo(BaiHat song) {
        if (song == null) return;

        tvSongName.setText(song.getTenBaiHat());

        String artist = song.getCaSi();
        tvArtist.setText(artist != null && !artist.isEmpty() ? artist : "Chưa rõ ca sĩ");

        Glide.with(context)
                .load(song.getHinhAnh())
                .placeholder(R.drawable.default_category)
                .error(R.drawable.default_category)
                .circleCrop()
                .into(imgSongImage);
    }

    public void updatePlayPauseIcon(boolean isPlaying) {
        if (imgPlayPause != null) {
            imgPlayPause.setImageResource(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play);
        }
    }

    public void updateFavoriteIcon(boolean isFavorite) {
        if (imgFavorite != null) {
            if (isFavorite) {
                imgFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.holo_red_dark));
            } else {
                imgFavorite.setColorFilter(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
        }
    }

    public void setTotalTime(int duration) {
        if (seekBar != null) {
            seekBar.setMax(duration);
        }
        if (tvTotalTime != null) {
            tvTotalTime.setText(formatTime(duration));
        }
    }

    public void updateCurrentTime(int position) {
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(formatTime(position));
        }
        if (seekBar != null) {
            seekBar.setProgress(position);
        }
    }


    public void startSeekBarUpdate(MediaPlayerManager playerManager) {
        stopSeekBarUpdate();

        seekBarUpdater = new Runnable() {
            @Override
            public void run() {
                if (playerManager != null && playerManager.isPlaying()) {
                    int currentPosition = playerManager.getCurrentPosition();
                    updateCurrentTime(currentPosition);
                    handler.postDelayed(this, 100);
                }
            }
        };
        handler.post(seekBarUpdater);
    }

    public void stopSeekBarUpdate() {
        if (seekBarUpdater != null) {
            handler.removeCallbacks(seekBarUpdater);
        }
    }

    public void startRotation() {
        if (isRotating || imgSongImage == null) return;

        rotateAnimation = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        imgSongImage.startAnimation(rotateAnimation);
        isRotating = true;
    }

    public void stopRotation() {
        if (rotateAnimation != null && imgSongImage != null) {
            rotateAnimation.cancel();
            imgSongImage.clearAnimation();
            isRotating = false;
        }
    }

    public void cleanup() {
        stopSeekBarUpdate();
        stopRotation();
        handler.removeCallbacksAndMessages(null);
    }

    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}