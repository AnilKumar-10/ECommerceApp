package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.*;

public interface CategoryRepository extends MongoRepository<Category,String> {

    // Get subcategories of a category
    List<Category> findByParentId(String parentId);

    // Get category by name (optional - useful for validation or lookup)
    Category findByName(String name);

    List<Category> findByNameIgnoreCase(String name);
//    List<Category> findByParentId(String parentId);
}
