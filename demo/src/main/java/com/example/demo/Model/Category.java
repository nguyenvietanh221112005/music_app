package com.example.demo.Model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "theloai")
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_theloai")
  private Integer id;

  @Column(name = "ten_theloai", nullable = false)
  private String tenTheLoai;

  @Column(name = "hinh_anh")
  private String hinhAnh;

  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Song> songs;


}
