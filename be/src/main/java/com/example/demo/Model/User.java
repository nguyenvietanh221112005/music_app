package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String ten;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;


  private LocalDateTime createdAt = LocalDateTime.now();

}
