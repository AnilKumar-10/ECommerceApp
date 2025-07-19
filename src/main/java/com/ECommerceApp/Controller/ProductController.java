package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ProductCreationDto;
import com.ECommerceApp.DTO.ProductSearchResponseDto;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.ValueConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ProductController { // admin, seller

    @Autowired
    private ProductService productService;

    @PostMapping("/insertProduct")
    public ResponseEntity<?> insertProduct(@Valid @RequestBody ProductCreationDto product, BindingResult result){
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            return ResponseEntity.badRequest().body(errors);
        }
        return ResponseEntity.ok(productService.createProduct(product));
    }

    @PostMapping("/insertProducts")
    public ResponseEntity<?>  insertProducts(@Valid @RequestBody List<@Valid  ProductCreationDto> product){
        return ResponseEntity.ok(productService.createProductList(product));
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


    @PutMapping("/updateProduct")
    public ResponseEntity<?> updateProduct(@Valid @RequestBody ProductCreationDto product){
        return ResponseEntity.ok(productService.updateProduct(product));
    }



    @GetMapping("/deleteProduct/{productId}")
    public String deleteProduct(@PathVariable String productId){
        return productService.deleteProduct(productId);
    }


}
