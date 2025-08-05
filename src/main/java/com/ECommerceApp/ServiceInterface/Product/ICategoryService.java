package com.ECommerceApp.ServiceInterface.Product;

import com.ECommerceApp.Model.Product.Category;

import java.util.List;
import java.util.Set;

public interface ICategoryService {

    Category createCategory(Category category);

    String createCategoryList(List<Category> categories);

    List<Category> getAllCategories();

    Category getCategoryById(String id);

    List<Category> getSubCategories(String parentId);

    List<Category> getRootCategories();

    Category updateCategory(Category updatedCategory);

    String deleteCategory(String id);

    boolean isLeafCategory(String id);

    List<Category> getCategoryHierarchy(String categoryId);

    Set<String> getAllDescendantCategoryIds(String categoryId);

    String getRootCategoryId(List<String> categoryIds);

    Category getCategoryByName(String name);

    List<String> getAllSubCategoryIds(String rootId);

    List<Category> getCategoryByNameIgnoreCase(String name);
}
