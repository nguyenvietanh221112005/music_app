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
  public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
    Map<String, Object> response = new HashMap<>();

    User existingUser = userService.findByEmail(user.getEmail())
        .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

    if (!existingUser.getPassword().equals(user.getPassword())) {
      response.put("success", false);
      response.put("message", "Sai mật khẩu!");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    response.put("success", true);
    response.put("message", "Đăng nhập thành công!");
    response.put("user_id", existingUser.getId());
    response.put("ten", existingUser.getTen());
    response.put("email", existingUser.getEmail());

    return ResponseEntity.ok(response);
  }
}
