package com.ECommerceApp.Controller;

import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/insertCategory")
    public String  inserCategory(@RequestBody List<Category> categories){
        return categoryService.createCategoryList(categories);
    }
}
