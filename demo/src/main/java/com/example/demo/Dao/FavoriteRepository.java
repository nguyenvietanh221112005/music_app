package com.example.demo.Dao;

import com.example.demo.Model.Favorite;
import com.example.demo.Model.Song;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

  List<Favorite> findByUserId(Long userId);

  void deleteByUserIdAndSongId(Long userId, Long songId);

  boolean existsByUserIdAndSongId(Long userId, Long songId);
  
  @Query("""
      SELECT new com.example.demo.Model.Song(
        b.id, b.tenBaiHat, b.caSi, b.hinhAnh, b.link
      )
      FROM Favorite f
      JOIN Song b ON f.songId = b.id
      WHERE f.userId = :userId
    """)
  List<Song> findFavoriteSongsByUserId(@Param("userId") Long userId);
}
