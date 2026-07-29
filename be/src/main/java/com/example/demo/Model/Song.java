package com.example.demo.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "baihat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "ten_baihat", nullable = false)
  private String tenBaiHat;

  @Column(name = "hinh_anh")
  private String hinhAnh;

  @Column(name = "ca_si")
  private String caSi;

  @Column(name = "link")
  private String link;

  @Column(name = "luot_thich")
  private Integer luotThich = 0;

  @ManyToOne
  @JoinColumn(name = "id_theloai", referencedColumnName = "id_theloai")
  @JsonIgnore
  private Category category;
  public Song(Integer id, String tenBaiHat, String caSi, String hinhAnh, String link) {
    this.id = id;
    this.tenBaiHat = tenBaiHat;
    this.caSi = caSi;
    this.hinhAnh = hinhAnh;
    this.link = link;
  }
}
