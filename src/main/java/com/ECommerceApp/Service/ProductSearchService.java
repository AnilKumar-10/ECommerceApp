package com.ECommerceApp.Service;

import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Repository.CategoryRepository;
import com.ECommerceApp.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductSearchService {

    @Autowired
    public ProductRepository productRepository;
    @Autowired
    public ProductService productService;
    @Autowired
    public CategoryService categoryService;
    @Autowired
    public CategoryRepository categoryRepository;

    // this will return the products that contain the category name.
    public List<Product> getProductsByCategoryName(String categoryName) {
        List<Category> rootCategory = categoryService.getCategoryByNameIgnoreCase(categoryName);
        System.out.println("root: "+rootCategory);
        if (rootCategory == null) return Collections.emptyList();
        Set<String> allCategoryIds = new HashSet<>();
        for (Category category : rootCategory) {
            allCategoryIds.addAll(categoryService.getAllSubCategoryIds(category.getId()));
//            allCategoryIds.add(category.getId());
        }
//        List<String> allCategoryIds = categoryService.getAllSubCategoryIds(rootCategory.getId());
        System.out.println("all categories id list: "+allCategoryIds);
        return productService.getProductContainsAnyCategory(new ArrayList<>(allCategoryIds));
    }



    public List<Product> getProductsByMultipleCategoryNames(List<String> categoryNames) {
        Set<String> categoryIds = new HashSet<>();
        for (String name : categoryNames) {
            Category root = categoryService.getCategoryByNameIgnoreCase(name);
            if (root != null) {
                categoryIds.addAll(categoryService.getAllSubCategoryIds(root.getId()));
            }
        }

        // Now fetch only products whose categoryIds contain *all* the categoryIds
        return productService.getProductContainsAllCategory(new ArrayList<>(categoryIds));
    }




}
