package com.ECommerceApp.Service;

import com.ECommerceApp.Exceptions.Product.CategoryNotFoundException;
import com.ECommerceApp.Exceptions.Product.RootCategoryNotFoundException;
import com.ECommerceApp.Model.Product.Category;
import com.ECommerceApp.Repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.*;
@Slf4j
@Service
public class  CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public String createCategoryList(List<Category> categories) {
        int c=0;
        for(Category category : categories){
            categoryRepository.save(category);
            c++;
        }
        if(c==categories.size()){
            return "All "+c+" Categories objects inserted successfully";
        }
        return "Something went wrong.";
    }

    // 2. Get all categories
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // 3. Get category by ID
    public Category getCategoryById(String id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found."));
    }

    // 4. Get subcategories of a parent
    public List<Category> getSubCategories(String parentId) {
        List<Category> categories = categoryRepository.findByParentId(parentId);
        log.info("The subCategories of: "+parentId+" are: "+categories);
        return categories;
    }

    // 5. Get root categories (parentId = null)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentId(null);
    }

    // 6. Update category name or parent
    public Category updateCategory(Category updatedCategory) {
        Category existing = getCategoryById(updatedCategory.getId());
        existing.setName(updatedCategory.getName());
        existing.setParentId(updatedCategory.getParentId());
        return categoryRepository.save(existing);
    }

    // 7. Delete category by ID (and optionally all subcategories recursively)
    public String  deleteCategory(String id) {
        log.warn("deleting the category: "+id);
        Category category = getCategoryById(id);
        deleteSubCategoriesRecursively(id); // delete all its children recursively
        categoryRepository.deleteById(id);
        return "Category and its sub Categories are deleted..";
    }

    private void deleteSubCategoriesRecursively(String parentId) {
        List<Category> subcategories = getSubCategories(parentId);
        log.warn("deleting the sub categories of: "+parentId+" ( "+subcategories+" )");
        for (Category sub : subcategories) {
            deleteSubCategoriesRecursively(sub.getId());
            categoryRepository.deleteById(sub.getId());
        }
    }

    // 8. Check if a category is a leaf category (no children)
    public boolean isLeafCategory(String id) {
        return getSubCategories(id).isEmpty();
    }

    // 9. Get full hierarchy path of a category (bottom to top)
    public List<Category> getCategoryHierarchy(String categoryId) {
        List<Category> hierarchy = new ArrayList<>();
        Category current = getCategoryById(categoryId);
        while (current != null) {
            hierarchy.add(current);
            if (current.getParentId() == null) break;
            current = categoryRepository.findById(current.getParentId()).orElse(null);
        }
        Collections.reverse(hierarchy);
        log.info("The category hierarchy of: "+categoryId+" is  "+hierarchy);
        return hierarchy;
    }

    // 10. Get all descendant category IDs (used in filtering products under nested categories)
    public Set<String> getAllDescendantCategoryIds(String categoryId) {
        Set<String> ids = new HashSet<>();
        getAllDescendantsRecursive(categoryId, ids);
        return ids;
    }

    private void getAllDescendantsRecursive(String parentId, Set<String> ids) {
        List<Category> children = getSubCategories(parentId);
        for (Category child : children) {
            ids.add(child.getId());
            getAllDescendantsRecursive(child.getId(), ids);
        }
    }

    // this will get the root category id in that list
    // if the given list doesn't contain the parent Category then based on the list it find out the root categoryId.
    public String getRootCategoryId(List<String> categoryIds) {
        // this will find the root categoryId in that list (only if the list contains the root id)
        System.out.println("inside getRoot: "+categoryIds);
        for (String id : categoryIds) {
            Category cat = categoryRepository.findById(id).orElse(null);
            System.out.println("for1: "+cat);
            if (cat != null && cat.getParentId() == null) {
                return cat.getId(); // It's a root category
            }
        }

        // if none marked as root, walk up to find root
        for (String id : categoryIds) {
            Category cat = categoryRepository.findById(id).orElse(null);
            System.out.println("for2: "+cat);
            while (cat != null && cat.getParentId() != null) {
                cat = categoryRepository.findById(cat.getParentId()).orElse(null);
            }
            if (cat != null) return cat.getId();
        }

        throw new RootCategoryNotFoundException("No valid root category found for product");
    }


    public Category getCategoryByName(String name){
        return categoryRepository.findByName(name);
    }


    public List<String> getAllSubCategoryIds(String rootId) {
        List<String> result = new ArrayList<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(rootId);

        while (!queue.isEmpty()) {
            String currentId = queue.poll();
            result.add(currentId);
            List<Category> children = categoryRepository.findByParentId(currentId);
            for (Category sub : children) {
                queue.add(sub.getId());
            }
        }
        log.info("All sub categories of the root : "+rootId+" is :"+result);
        return result;
    }


    public List<Category> getCategoryByNameIgnoreCase(String name){
        return categoryRepository.findByNameIgnoreCase(name);
    }



}
