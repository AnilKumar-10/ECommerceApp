package com.ECommerceApp.Service;

import com.ECommerceApp.Model.Category;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Repository.CategoryRepository;
import com.ECommerceApp.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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



//    public List<Product> getProductsByMultipleCategoryNames(List<String> categoryNames) {
//        Set<String> categoryIds = new HashSet<>();
//        for (String name : categoryNames) {
//            Category root = categoryService.getCategoryByNameIgnoreCase(name);
//            if (root != null) {
//                categoryIds.addAll(categoryService.getAllSubCategoryIds(root.getId()));
//            }
//        }
//
//        // Now fetch only products whose categoryIds contain *all* the categoryIds
//        return productService.getProductContainsAllCategory(new ArrayList<>(categoryIds));
//    }

//    public List<String> resolveCategoryIds(List<String> categoryNames) {
//        List<Category> categories = new ArrayList<>();
//        System.out.println("cateG: "+categories);
//        for (String name : categoryNames) {
//            categories.addAll(categoryService.getCategoryByNameIgnoreCase(name));
//        }
//
//        // Get combinations that match all input names in path
//        return getMatchingLeafCategoryIds(categoryNames, categories);
//    }
//
//    private List<String> getMatchingLeafCategoryIds(List<String> names, List<Category> allMatches) {
//        System.out.println("names: "+names+"  "+allMatches);
//        List<String> matchingLeafIds = new ArrayList<>();
//
//        for (Category cat : allMatches) {
//            List<String> path = new ArrayList<>();
//            Category current = cat;
//            while (current != null) {
//                path.add(current.getName());
//                System.out.println("path: "+path);
//                current = categoryService.getCategoryById(current.getParentId());
//            }
//            // Check if all input names exist in this category path
//            if (names.stream().allMatch(n -> path.contains(n))) {
//                matchingLeafIds.add(cat.getId());
//                System.out.println("match: "+matchingLeafIds);
//            }
//        }
//        System.out.println(" returm before: "+matchingLeafIds);
//        return matchingLeafIds;
//    }



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



    public List<Product> searchProductsByCategoryNames(List<String> inputNames) {
//        System.out.println("in search : "+inputNames);
        List<List<String>> requiredIdGroups = resolveCategoryIdGroups(inputNames);
        // If no matching groups found, return empty
//        System.out.println("requ: "+requiredIdGroups);
        if (requiredIdGroups.isEmpty()) return Collections.emptyList();

        List<Product> allMatchingProducts = new ArrayList<>();
        for (List<String> requiredIds : requiredIdGroups) {
            // Find products that contain all IDs in this group
            allMatchingProducts.addAll(
                    productRepository.findByCategoryIdsContainingAll(requiredIds)
            );
        }
        return new ArrayList<>(new HashSet<>(allMatchingProducts));
    }






}
