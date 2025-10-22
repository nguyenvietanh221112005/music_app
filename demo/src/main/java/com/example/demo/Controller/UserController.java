package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public String register(@RequestBody User user) {
    if (userService.findByEmail(user.getEmail()).isPresent()) {
      return "Email đã được sử dụng!";
    }
    userService.createUser(user);
    return "Đăng ký thành công!";
  }

  @PostMapping("/login")
  public String login(@RequestBody User user) {
    User existingUser = userService.findByEmail(user.getEmail())
        .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

    if (!existingUser.getPassword().equals(user.getPassword())) {
      return "Sai mật khẩu!";
    }

    return "Đăng nhập thành công! Xin chào, " + existingUser.getTen();
  }
}
