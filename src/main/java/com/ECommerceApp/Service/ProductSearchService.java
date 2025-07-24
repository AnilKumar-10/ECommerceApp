package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Model.Product.Category;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Repository.CategoryRepository;
import com.ECommerceApp.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;



import java.util.*;

import java.util.stream.Collectors;
@Slf4j
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
        log.info("getting the product based on the category: "+categoryName);
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


    // this method gets all categories ids based on the names.
    public List<List<String>> resolveCategoryIdGroups(List<String> inputNames) {
        log.info("getting the all categories of the input categories: "+inputNames);
        List<Category> matchedCategories = new ArrayList<>();
        for (String name : inputNames) {
            matchedCategories.addAll(categoryService.getCategoryByNameIgnoreCase(name));
        }
        List<List<String>> idGroups = new ArrayList<>();
        for (Category category : matchedCategories) {
            List<String> fullPathNames = new ArrayList<>();
            List<String> fullPathIds = new ArrayList<>();
            Category current = category;
            while (current != null) {
                fullPathNames.add(current.getName());
                fullPathIds.add(current.getId());
                if(current.getParentId() == null){
                    break;
                }
                current = categoryService.getCategoryById(current.getParentId());
            }
            // Match only if all inputNames are present in the path
            if (inputNames.stream()
                    .map(String::toLowerCase)
                    .allMatch(n -> fullPathNames.stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet())
                            .contains(n))) {
                idGroups.add(fullPathIds); // one group of required categoryIds
            }
        }
        log.info("The id groups are: "+idGroups);
        return idGroups;
    }



    public List<Product> searchProductsByCategoryNames(List<String> inputNames,String brand) {
        log.info("Searching the product based on the category name and the brand");
        List<List<String>> requiredIdGroups = resolveCategoryIdGroups(inputNames);
        // If no matching groups found, return empty
        if (requiredIdGroups.isEmpty())  throw
                new ProductNotFoundException("The product you are trying is not present try to search categories like Clothes, Footwear, Electronics etc");

        List<Product> allMatchingProducts = new ArrayList<>();
        for (List<String> requiredIds : requiredIdGroups) {
            // Find products that contain all IDs in this group
//            allMatchingProducts.addAll(
//                    productRepository.findByCategoryIdsContainingAll(requiredIds)
//            );
            //=======
            if (brand != null && !brand.isBlank()) {
                allMatchingProducts.addAll(
                        productRepository.findByCategoryIdsContainingAllAndBrandIgnoreCase(requiredIds, brand)
                );
            } else {
                allMatchingProducts.addAll(
                        productRepository.findByCategoryIdsContainingAll(requiredIds)
                );
            }
        }
        return new ArrayList<>(new HashSet<>(allMatchingProducts));
    }



    public List<ProductSearchResponse> getProductByBrand(String brandName){
        log.info("Getting the products based on the brand");
        return productRepository.findByBrandIgnoreCase(brandName);
    }


    public Page<ProductSearchResponse> getAllProducts(int page, int size) {
        log.info("Getting all the products present in the db with paging ");
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable); // Update this call

        List<ProductSearchResponse> dtoList = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            ProductSearchResponse dto = new ProductSearchResponse();
            BeanUtils.copyProperties(product, dto);
            dtoList.add(dto);
        }

        return new PageImpl<>(dtoList, pageable, productPage.getTotalElements());
    }

}
