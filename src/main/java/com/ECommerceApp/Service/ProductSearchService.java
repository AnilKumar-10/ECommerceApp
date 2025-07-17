package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.ProductSearchResponseDto;
import com.ECommerceApp.Exceptions.ProductNotFoundException;
import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Repository.CategoryRepository;
import com.ECommerceApp.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

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



    public List<List<String>> resolveCategoryIdGroups(List<String> inputNames) {
//        System.out.println("in resolve: "+inputNames);
        List<Category> matchedCategories = new ArrayList<>();
        for (String name : inputNames) {
            matchedCategories.addAll(categoryService.getCategoryByNameIgnoreCase(name));
        }
        List<List<String>> idGroups = new ArrayList<>();
//        System.out.println("matched: "+matchedCategories);
        for (Category category : matchedCategories) {
            List<String> fullPathNames = new ArrayList<>();
            List<String> fullPathIds = new ArrayList<>();
            Category current = category;
            while (current != null) {
                fullPathNames.add(current.getName());
                fullPathIds.add(current.getId());
//                System.out.println("current: "+current );
                if(current.getParentId() == null){
                    break;
                }
                current = categoryService.getCategoryById(current.getParentId());
            }
//            System.out.println("pathnames: "+fullPathNames);
//            System.out.println("pathids: "+fullPathIds);
            // Match only if all inputNames are present in the path
            if (inputNames.stream()
                    .map(String::toLowerCase)
                    .allMatch(n -> fullPathNames.stream()
                            .map(String::toLowerCase)
                            .collect(Collectors.toSet())
                            .contains(n))) {
                idGroups.add(fullPathIds); // one group of required categoryIds
//                System.out.println("idgroups: "+idGroups);
            }
        }
//        System.out.println("grops: "+idGroups);
        return idGroups;
    }



    public List<Product> searchProductsByCategoryNames(List<String> inputNames,String brand) {
//        System.out.println("in search : "+inputNames);
        List<List<String>> requiredIdGroups = resolveCategoryIdGroups(inputNames);
        // If no matching groups found, return empty
//        System.out.println("requ: "+requiredIdGroups);
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



    public List<ProductSearchResponseDto> getProductByBrand(String brandName){
        return productRepository.findByBrandIgnoreCase(brandName);
    }


}
