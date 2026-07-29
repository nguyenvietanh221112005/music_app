package com.example.demo.Dao;

import com.example.demo.Model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;

public interface SongRepository extends JpaRepository<Song, Integer> {

  @Query("SELECT s FROM Song s WHERE s.luotThich > 0 ORDER BY s.luotThich DESC")
  List<Song> findAllByLuotThichDesc();
  
  @Query(value = "SELECT * FROM baihat ORDER BY RAND()", nativeQuery = true)
  List<Song> findAllRandom();

  @Query("SELECT s FROM Song s WHERE s.category.id = :categoryId ORDER BY s.luotThich DESC")
  List<Song> findByCategoryId(@Param("categoryId") int categoryId);

  @Query("SELECT s FROM Song s WHERE LOWER(s.tenBaiHat) LIKE LOWER(CONCAT('%', :keyword, '%'))")
  List<Song> findByTenBaiHatContainingIgnoreCase(@Param("keyword") String keyword);

}
