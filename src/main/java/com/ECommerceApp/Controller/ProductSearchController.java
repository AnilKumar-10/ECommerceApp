package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.ProductSearchResponseDto;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Service.ProductSearchService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@RestController
public class ProductSearchController {

    @Autowired
    private ProductSearchService productSearchService;



    @GetMapping("/product/{name}")
    public List<ProductSearchResponseDto> getProductByCategoryName(@PathVariable String name){
        List<Product> products = productSearchService.getProductsByCategoryName(name);
        System.out.println("products are: "+products);
        List<ProductSearchResponseDto> productSearchDtos  = new ArrayList<>();
        for(Product product : products){
            ProductSearchResponseDto productSearchDto = new ProductSearchResponseDto();
            BeanUtils.copyProperties(product , productSearchDto);
            productSearchDtos.add(productSearchDto);
        }
        return productSearchDtos;
    }


    @GetMapping("/search")
    public List<ProductSearchResponseDto> searchProductsByCategoryNames(
            @RequestParam List<String> categories,
            @RequestParam(name = "sortOrder", required = false, defaultValue = "asc") String sortOrder,
            @RequestParam(name = "sortBy", required = false, defaultValue = "rating") String sortBy, HttpServletRequest httpServletRequest) {
        System.out.println("http url: "+httpServletRequest.getQueryString());
        System.out.println("sort: "+sortOrder+"  soryby: "+sortBy);
        List<ProductSearchResponseDto> productSearchDtos = new ArrayList<>();
        List<Product> products = productSearchService.searchProductsByCategoryNames(categories);

        for (Product product : products) {
            ProductSearchResponseDto dto = new ProductSearchResponseDto();
            BeanUtils.copyProperties(product, dto);
            productSearchDtos.add(dto);
        }

        Comparator<ProductSearchResponseDto> comparator;
        if ("rating".equalsIgnoreCase(sortBy)) {
            comparator = Comparator.comparingDouble(ProductSearchResponseDto::getRating);
        } else {
            comparator = Comparator.comparingDouble(ProductSearchResponseDto::getPrice);
        }

        if ("desc".equalsIgnoreCase(sortOrder)) {
            comparator = comparator.reversed();
        }

        productSearchDtos.sort(comparator);

        return productSearchDtos;
    }


}
