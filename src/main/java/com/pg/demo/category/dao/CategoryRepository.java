package com.pg.demo.category.dao;

import com.pg.demo.category.dataobject.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query(value = "select * from ofr_category where c_level = 'ONE'", nativeQuery = true)
  List<Category> fetchAll();
}

