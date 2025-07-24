package com.ECommerceApp.Controller.Product;


import com.ECommerceApp.Model.Product.Category;
import com.ECommerceApp.Service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
public class CategoryController { // admin,seller

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/createCategory")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody Category category){
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    @PostMapping("/createCategories")
    public ResponseEntity<?>  insertCategories(@Valid @RequestBody List<@Valid Category> categories){
        return ResponseEntity.ok(categoryService.createCategoryList(categories));
    }

    @GetMapping("/getCategory/{categoryId}")
    public Category getCategory(@PathVariable String categoryId){
        return categoryService.getCategoryById(categoryId);
    }

    @GetMapping("/getAllCategories")
    public List<Category> getAllCategories(){
        return categoryService.getAllCategories();
    }


    @PutMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category){
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    @DeleteMapping("/deleteCategory/{categoryId}")
    public String deleteCategory(@PathVariable String categoryId){
        return categoryService.deleteCategory(categoryId);
    }

    @GetMapping("/getHierarchy/{categoryId}")
    public List<Category> getCategoryHierarchy(@PathVariable String categoryId){
        return categoryService.getCategoryHierarchy(categoryId);
    }

    @GetMapping("/getAllSubCategories/{categoryId}")
    public Set<String > getAllSubCategories(@PathVariable String categoryId){
        return categoryService.getAllDescendantCategoryIds(categoryId);
    }

    @GetMapping("/getCategoryByName/{categoryName}")
    public Category getCategoryByName(@PathVariable String categoryName){
        return categoryService.getCategoryByName(categoryName);
    }
}
