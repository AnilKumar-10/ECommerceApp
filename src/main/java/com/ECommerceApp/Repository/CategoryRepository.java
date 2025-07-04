package com.ECommerceApp.Repository;

import com.ECommerceApp.Model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category,String> {
}
