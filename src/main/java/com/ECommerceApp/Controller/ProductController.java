package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ProductRequest;
import com.ECommerceApp.DTO.ProductSearchResponseDto;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/insertProduct")
    public Product insertProduct(@RequestBody ProductRequest product){
        return productService.createProduct(product);
    }

    @PostMapping("/insertProducts")
    public String  insertProducts(@RequestBody List<ProductRequest> product){
        return productService.createProductList(product);
    }

    @GetMapping("/getProduct/{id}")
    public ProductSearchResponseDto getProduct(@PathVariable String id){
        Product product = productService.getProductById(id);
        ProductSearchResponseDto productSearchResponseDto = new ProductSearchResponseDto();
        BeanUtils.copyProperties(product,productSearchResponseDto);
        return productSearchResponseDto;
    }

    @GetMapping("/getAllProducts")
    public List<Product> get(){
        return productService.getAllProducts();
    }


    public String deleteProduct(@PathVariable String productId){
        return productService.deleteProduct(productId);
    }


}
