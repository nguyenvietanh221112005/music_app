package com.example.demo.Service;

import com.example.demo.Model.Category;
import com.example.demo.Dao.CategoryRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public CategoryService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  public List<Category> getAllCategories() {
    return categoryRepository.findAll();
  }
}
