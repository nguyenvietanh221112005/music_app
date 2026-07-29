package com.example.demo.Service;

import com.example.demo.Model.User;
import com.example.demo.Dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public Optional<User> getUserById(Integer id) {
    return userRepository.findById(id);
  }

  public User createUser(User user) {
    return userRepository.save(user);
  }

  public User updateUser(Integer id, User newUser) {
    return userRepository.findById(id).map(user -> {
      user.setTen(newUser.getTen());
      user.setEmail(newUser.getEmail());
      user.setPassword(newUser.getPassword());
      return userRepository.save(user);
    }).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public void deleteUser(Integer id) {
    userRepository.deleteById(id);
  }

  public Optional<User> findByEmail(String email) {
    return userRepository.findByEmail(email);
  }

}
