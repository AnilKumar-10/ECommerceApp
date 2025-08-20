package com.ECommerceApp.ServiceInterface.Product;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProductService {

    Product createProduct(ProductCreationRequest request);

    String createProductList(List<ProductCreationRequest> products);

    List<Product> getAllProducts();

    Product getProductById(String id);

    Product updateProduct(ProductCreationRequest request);

    String deleteProduct(String id);

//    double calculateAverageRating(String productId);

    double getProductPrice(String id);

    List<Product> getProductContainsAnyCategory(List<String> allCategoryIds);

    List<Product> getProductContainsAllCategory(List<String> allCategoryIds);

    Page<Product> getAllProducts(Pageable pageable);

    boolean checkStockAvailability(String productId);

    long getTotalCountOfProductBySeller(String sellerId);

    List<Product> getProductsByCategoryAndBrand(List<String> categoryIds, String brand);

    List<ProductSearchResponse> getProductsByBrand(String brandName);

    public Product saveProduct(Product product);

    List<Product> getProductsBySellerId(String sellerId);



}

