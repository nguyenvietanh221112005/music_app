package com.example.demo.Controller;

import com.example.demo.Model.Favorite;
import com.example.demo.Model.Song;
import com.example.demo.Service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {

  @Autowired
  private FavoriteService favoriteService;


  @GetMapping("/{userId}")
  public ResponseEntity<List<Song>> getFavoriteSongs(@PathVariable Long userId) {
    List<Song> favoriteSongs = favoriteService.getFavoriteSongsByUser(userId);
    return ResponseEntity.ok(favoriteSongs);
  }


  @PostMapping("/add/{userId}/{songId}")
  public ResponseEntity<?> addFavorite(@PathVariable Long userId, @PathVariable Long songId) {
    Favorite added = favoriteService.addFavorite(userId, songId);
    if (added == null) {
      return ResponseEntity.badRequest().body("Bài hát đã được yêu thích!");
    }
    return ResponseEntity.ok(added);
  }
  
  @DeleteMapping("/delete/{userId}/{songId}")
  public ResponseEntity<String> deleteFavorite(@PathVariable Long userId, @PathVariable Long songId) {
    favoriteService.deleteFavorite(userId, songId);
    return ResponseEntity.ok("Đã xóa khỏi danh sách yêu thích");
  }
}
