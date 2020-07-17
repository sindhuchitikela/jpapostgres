package com.pg.demo.category.controller;

import com.pg.demo.category.dataobject.Category;
import com.pg.demo.category.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Tag(name = "category", description = "Service that provides APIs to create, fetch, delete Categories")
public class CategoryController {

  private final CategoryService categoryService;

  @Autowired
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }


  @GetMapping(value = "{id}")
  @Operation(summary = "Fetch category by id", description = "Fetches category by given id along with its children", tags = "category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully fetched requested Category",
          content = @Content(schema = @Schema(implementation = Category.class)))
  })
  public Category getCategory(
      @Parameter(description = "Id of the category to fetch") @PathVariable(required = true) Long id,
      @Parameter(description = "Boolean value to indicate whether to fetch categories with INACTIVE status, If true, fetches categories in both ACTIVE and INACTIVE status") @RequestParam(value = "fetchInactive", required = false) Boolean fetchInactive
  ) {
    return categoryService.fetchCategory(id);
  }

  @DeleteMapping(value = "{id}")
  @Operation(summary = "Delete category by id", description = "Deletes category by given id if it neither have any children nor any offers associated with it", tags = "category")
  public void deleteCategory(
      @Parameter(description = "Id of the category to be deleted") @PathVariable(required = true) Long id) {
    categoryService.deleteCategory(id);
  }


  @PostMapping(consumes = "application/json")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category created",
          content = @Content(schema = @Schema(implementation = Category.class)))
  })
  @Operation(summary = "Save Category", description = "Saves a given category and returns the saved data", tags = "category")
  public Category saveCategory(@Parameter(description = "Category to create",
      required = true, schema = @Schema(implementation = Category.class)) @RequestBody @Valid Category category) {
    return categoryService.saveCategory(category);
  }

  @PutMapping(consumes = "application/json")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category updated",
          content = @Content(schema = @Schema(implementation = Category.class)))
  })
  @Operation(summary = "Update Category", description = "Updates a given category and returns the saved data", tags = "category")
  public Category updateCategory(@Parameter(description = "Category to update",
      required = true, schema = @Schema(implementation = Category.class)) @RequestBody @Valid Category category) {
    return categoryService.saveCategory(category);
  }
}