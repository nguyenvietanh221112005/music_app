package com.example.demo.Service;

import com.example.demo.Dao.SongRepository;
import com.example.demo.Model.Song;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongService {

  @Autowired
  private SongRepository songRepository;

  public List<Song> getTopSongs() {
    List<Song> danhSach = songRepository.findAllByLuotThichDesc();

    if (danhSach.isEmpty()) {
      return songRepository.findAllRandom();
    }

    return danhSach;
  }


  public SongService(SongRepository songRepository) {
    this.songRepository = songRepository;
  }

  public List<Song> getSongsByCategory(Integer category) {
    return songRepository.findByCategoryId(category);
  }

  public List<Song> searchSongs(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return List.of();
    }
    return songRepository.findByTenBaiHatContainingIgnoreCase(keyword);
  }
}
