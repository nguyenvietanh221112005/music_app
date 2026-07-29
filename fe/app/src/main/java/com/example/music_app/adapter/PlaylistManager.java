package com.example.music_app.adapter;

import com.example.music_app.model.BaiHat;
import java.util.ArrayList;

public class PlaylistManager {

    private ArrayList<BaiHat> playlist;
    private int currentPosition;
    private PlaylistListener listener;

    public interface PlaylistListener {
        void onSongChanged(BaiHat song, int position);
        void onPlaylistEmpty();
    }

    public PlaylistManager(PlaylistListener listener) {
        this.listener = listener;
        this.playlist = new ArrayList<>();
        this.currentPosition = 0;
    }

    public void setPlaylist(ArrayList<BaiHat> playlist, int startPosition) {
        if (playlist != null) {
            this.playlist = new ArrayList<>(playlist);
            this.currentPosition = startPosition;
        }
    }



    public BaiHat getNextSong() {
        if (playlist == null || playlist.isEmpty()) {
            if (listener != null) {
                listener.onPlaylistEmpty();
            }
            return null;
        }

        currentPosition = (currentPosition + 1) % playlist.size();
        BaiHat nextSong = playlist.get(currentPosition);

        if (listener != null) {
            listener.onSongChanged(nextSong, currentPosition);
        }

        return nextSong;
    }

    public BaiHat getPreviousSong() {
        if (playlist == null || playlist.isEmpty()) {
            if (listener != null) {
                listener.onPlaylistEmpty();
            }
            return null;
        }

        currentPosition = (currentPosition - 1 + playlist.size()) % playlist.size();
        BaiHat previousSong = playlist.get(currentPosition);

        if (listener != null) {
            listener.onSongChanged(previousSong, currentPosition);
        }

        return previousSong;
    }


}