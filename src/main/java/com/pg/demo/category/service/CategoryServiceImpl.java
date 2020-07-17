package com.pg.demo.category.service;

import com.pg.demo.category.dao.CategoryRepository;
import com.pg.demo.category.dataobject.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {
  private static final Logger             LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);
  private final        CategoryRepository categoryRepository;

  @Autowired
  public CategoryServiceImpl(
      CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }


  @Override
  public Category fetchCategory(Long id) {
      return categoryRepository.findById(id).get();
  }


  @Override
  public void deleteCategory(Long id) {
    Optional<Category> existingCategory = categoryRepository.findById(id);
    if (existingCategory.isEmpty()) {
      throw new RuntimeException(
          "Delete failed, no category found with this id: " + id);
    } else {
      categoryRepository.deleteById(id);
    }
  }


  @Override
  public Category saveCategory(Category category) {

    /* If the incoming request doesn't set parent Id, set root as parent by default */
    if (category.getParentId() == null) {
      category.setParentId(0L);
    }

    if (category.getId() == null) { //insert new category
      return categoryRepository.save(category);
    } else { //update existing category
      try {
        return categoryRepository.findById(category.getId()).get();
      } catch (NoSuchElementException noElementException) {
        throw new RuntimeException(
            "Exception occurred while updating category. No category found with id: " + category.getId(),
            noElementException);
      }
    }
  }

}
