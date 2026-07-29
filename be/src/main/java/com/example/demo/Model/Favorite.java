package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "yeuthich")
public class Favorite {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // id tự tăng trong bảng

  @Column(name = "id_user", nullable = false)
  private Long userId;

  @Column(name = "id_baihat", nullable = false)
  private Long songId;

  public Favorite(Long userId, Long songId) {
    this.userId = userId;
    this.songId = songId;
  }
}
