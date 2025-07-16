package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/createCategory")
    public Category insertCategory(@RequestBody Category category){
        return categoryService.createCategory(category);
    }

    @PostMapping("/createCategories")
    public String  insertCategories(@RequestBody List<Category> categories){
        return categoryService.createCategoryList(categories);
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
    public Category updateCategory(@RequestBody Category category){
        return categoryService.updateCategory(category);
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
