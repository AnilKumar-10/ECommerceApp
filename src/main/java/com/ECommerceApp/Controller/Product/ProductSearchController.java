package com.ECommerceApp.Controller.Product;

import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Service.ProductSearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@RestController
public class ProductSearchController { // everyone

    @Autowired
    private ProductSearchService productSearchService;

    @GetMapping("/product/{name}")
    public List<ProductSearchResponse> getProductByCategoryName(@PathVariable String name){
        List<Product> products = productSearchService.getProductsByCategoryName(name);
        System.out.println("products are: "+products);
        List<ProductSearchResponse> productSearchDtos  = new ArrayList<>();
        for(Product product : products){
            ProductSearchResponse productSearchDto = new ProductSearchResponse();
            BeanUtils.copyProperties(product , productSearchDto);
            productSearchDtos.add(productSearchDto);
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
            @RequestParam(name = "sortBy", required = false) String sortBy,
            HttpServletRequest httpServletRequest) {
        return productSearchService.searchRequest(categories, brand, sortOrder, sortBy, httpServletRequest);
    }

    @GetMapping("/searchBrand/{brandName}")
    public ResponseEntity<?> getProductByBrand(@PathVariable String brandName) {
        List<ProductSearchResponse> productSearchResponses = productSearchService.getProductByBrand(brandName);
        if(productSearchResponses.isEmpty()){
            return ResponseEntity.ok("There is no products found with: "+brandName);
        }
        return ResponseEntity.ok(productSearchResponses);
    }

    @GetMapping("/viewAll")
//  viewAll?page=1&size=5
//  the size decides the no of objects to be displayed in the page.
    public Page<ProductSearchResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        System.out.println(page+" : "+size);
        return productSearchService.getAllProducts(page, size);
    }


    @GetMapping("/UserFeed")
    public List<ProductSearchResponse> feedByWishList(){
        return productSearchService.feedByWishProducts();
    }


}
