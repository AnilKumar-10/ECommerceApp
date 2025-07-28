package com.ECommerceApp.ServiceImplementation;

import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Model.Product.Category;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Repository.CategoryRepository;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.ServiceInterface.IProductSearchService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import com.ECommerceApp.ServiceInterface.*;



import java.util.*;

import java.util.stream.Collectors;
@Slf4j
@Service
public class ProductSearchService implements IProductSearchService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IProductService productService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private IWishListService wishListService;

    // this will return the products that contain the category name.
    public List<Product> getProductsByCategoryName(String categoryName) {
        log.info("getting the product based on the category: "+categoryName);
        List<Category> rootCategory = categoryService.getCategoryByNameIgnoreCase(categoryName);
        System.out.println("root: "+rootCategory);
        if (rootCategory == null) return Collections.emptyList();
        Set<String> allCategoryIds = new HashSet<>();
        for (Category category : rootCategory) {
            allCategoryIds.addAll(categoryService.getAllSubCategoryIds(category.getId()));
        }
        System.out.println("all categories id list: "+allCategoryIds);
        return productService.getProductContainsAnyCategory(new ArrayList<>(allCategoryIds));
    }


    // this method gets all categories ids based on the names.
    // this will return the categories id groups based on the names.
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

            if (brand != null && !brand.isBlank()) {
                allMatchingProducts.addAll(productRepository.findByCategoryIdsContainingAllAndBrandIgnoreCase(requiredIds, brand));
            } else {
                allMatchingProducts.addAll(productService.getProductContainsAllCategory(requiredIds));
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


    public List<ProductSearchResponse> feedByWishProducts(){
        log.info("Getting the search feed based on the Wish list items");
        List<String> productIds = wishListService.getWishlistProductIdsByBuyer("USER1019");
        List<String> rootIds = new ArrayList<>();
        for(String productId : productIds){
            String root = categoryService.getRootCategoryId(productService.getProductById(productId).getCategoryIds());
            rootIds.add(root);
        }
        List<Product>  products = productService.getProductContainsAnyCategory(rootIds);
        List<ProductSearchResponse> productSearchResponses = new ArrayList<>();
        for(Product product : products ){
            ProductSearchResponse productSearchResponse = new ProductSearchResponse();
            BeanUtils.copyProperties(product,productSearchResponse);
            productSearchResponses.add(productSearchResponse);
        }
        return productSearchResponses;
    }


    public List<ProductSearchResponse> searchRequest(List<String> categories, String brand, String sortOrder, String sortBy,
                                                     HttpServletRequest httpServletRequest){
        log.info("sortOrder: " + sortOrder + "  sortby: " + sortBy + "  brand: " + brand);
        List<ProductSearchResponse> productSearchDtos = new ArrayList<>();
        List<Product> products = searchProductsByCategoryNames(categories, brand);
        for (Product product : products) {
            ProductSearchResponse dto = new ProductSearchResponse();
            BeanUtils.copyProperties(product, dto);
            productSearchDtos.add(dto);
        }

        Comparator<ProductSearchResponse> comparator;
        if ("rating".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingDouble(ProductSearchResponse::getRating);
        } else {
            comparator = Comparator.comparingDouble(ProductSearchResponse::getPrice);
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }
        productSearchDtos.sort(comparator);
        return productSearchDtos;
    }


}
