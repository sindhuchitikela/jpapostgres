package com.pg.demo.category.service;

import com.pg.demo.category.dataobject.Category;

public interface CategoryService {
  Category fetchCategory(Long ids);
  void deleteCategory(Long id);
  Category saveCategory(Category category);
}
