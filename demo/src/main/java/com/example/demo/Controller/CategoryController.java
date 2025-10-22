package com.example.demo.Controller;

import com.example.demo.Model.Category;
import com.example.demo.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

  @Autowired
  private CategoryService CategoryService;

  @GetMapping
  public List<Category> getAllCategories() {
    return CategoryService.getAllCategories();
  }
}
