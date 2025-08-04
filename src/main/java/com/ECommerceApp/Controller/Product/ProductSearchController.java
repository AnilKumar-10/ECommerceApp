package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.ServiceInterface.IProductSearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@RestController
@RequestMapping("/browse")
public class ProductSearchController { // everyone

    @Autowired
    private IProductSearchService productSearchService;


    @GetMapping("/product/{name}")
    public List<ProductSearchResponse> getProductByCategoryName(@PathVariable String name) {
        List<Product> products = productSearchService.getProductsByCategoryName(name);
        List<ProductSearchResponse> productSearchDtos = new ArrayList<>();
        for (Product product : products) {
            ProductSearchResponse dto = new ProductSearchResponse();
            BeanUtils.copyProperties(product, dto);
            productSearchDtos.add(dto);
        }
        return productSearchDtos;
    }



    @GetMapping("/search")
    //    http://localhost:9090/search?categories=Shirts&sortOrder=desc
//    http://localhost:9090/search?categories=Shirts&sortOrder=desc&sortBy=rating
//    http://localhost:9090/search?categories=Footwear,Women&brand=Nike&sortOrder=desc&sortBy=rating
    public List<ProductSearchResponse> searchProductsByCategoryNames(
            @RequestParam List<String> categories,
            @RequestParam(name = "brand", required = false) String brand,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "sortBy", required = false) String sortBy) {
        return productSearchService.searchRequest(categories, brand, sortOrder, sortBy);
    }


    @GetMapping("/searchBrand/{brandName}")
    public ResponseEntity<?> getProductByBrand(@PathVariable String brandName) {
        List<ProductSearchResponse> results = productSearchService.getProductByBrand(brandName);
        if (results.isEmpty()) {
            return ResponseEntity.ok("There is no product found with brand: " + brandName);
        }
        return ResponseEntity.ok(results);
    }


    @GetMapping("/viewAll")
    //  viewAll?page=1&size=5
    public Page<ProductSearchResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productSearchService.getAllProducts(page, size);
    }


    @GetMapping("/UserFeed")
    public List<ProductSearchResponse> feedByWishList() {
        return productSearchService.feedByWishProducts();
    }

}
