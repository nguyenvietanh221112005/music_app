package com.example.demo.Service;

import com.example.demo.Model.Favorite;
import com.example.demo.Model.Song;
import com.example.demo.Dao.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FavoriteService {

  @Autowired
  private FavoriteRepository favoriteRepository;


  public List<Song> getFavoriteSongsByUser(Long userId) {
    return favoriteRepository.findFavoriteSongsByUserId(userId);
  }


  public List<Favorite> getFavoritesByUser(Long userId) {
    return favoriteRepository.findByUserId(userId);
  }


  public Favorite addFavorite(Long userId, Long songId) {
    if (!favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
      Favorite favorite = new Favorite(userId, songId);
      return favoriteRepository.save(favorite);
    }
    return null;
  }

  @Transactional
  public void deleteFavorite(Long userId, Long songId) {
    favoriteRepository.deleteByUserIdAndSongId(userId, songId);
  }
}
