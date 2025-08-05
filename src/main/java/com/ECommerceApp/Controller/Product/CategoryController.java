package com.ECommerceApp.Controller.Product;


import com.ECommerceApp.Model.Product.Category;
import com.ECommerceApp.ServiceInterface.Product.ICategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.*;


@RestController
@RequestMapping("/category")
public class CategoryController { // admin,seller

    @Autowired
    private ICategoryService categoryService;

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'INSERT')")
    @PostMapping("/createCategory")
    public ResponseEntity<?> insertCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.createCategory(category));
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'INSERT')")
    @PostMapping("/createCategories")
    public ResponseEntity<?> insertCategories(@Valid @RequestBody List<@Valid Category> categories) {
        return ResponseEntity.ok(categoryService.createCategoryList(categories));
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'READ')")
    @GetMapping("/getCategory/{categoryId}")
    public Category getCategory(@PathVariable String categoryId) {
        return categoryService.getCategoryById(categoryId);
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'READ')")
    @GetMapping("/getAllCategories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'UPDATE')")
    @PutMapping("/updateCategory")
    public ResponseEntity<?> updateCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(categoryService.updateCategory(category));
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'DELETE')")
    @DeleteMapping("/deleteCategory/{categoryId}")
    public String deleteCategory(@PathVariable String categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'READ')")
    @GetMapping("/getHierarchy/{categoryId}")
    public List<Category> getCategoryHierarchy(@PathVariable String categoryId) {
        return categoryService.getCategoryHierarchy(categoryId);
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'READ')")
    @GetMapping("/getAllSubCategories/{categoryId}")
    public Set<String> getAllSubCategories(@PathVariable String categoryId) {
        return categoryService.getAllDescendantCategoryIds(categoryId);
    }

    //  ADMIN, SELLER
    @PreAuthorize("hasPermission('CATEGORY', 'READ')")
    @GetMapping("/getCategoryByName/{categoryName}")
    public Category getCategoryByName(@PathVariable String categoryName) {
        return categoryService.getCategoryByName(categoryName);
    }
}
