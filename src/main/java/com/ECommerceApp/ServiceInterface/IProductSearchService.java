package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import org.springframework.data.domain.Page;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface IProductSearchService {

    List<Product> getProductsByCategoryName(String categoryName);

    List<List<String>> resolveCategoryIdGroups(List<String> inputNames);

    List<Product> searchProductsByCategoryNames(List<String> inputNames, String brand);

    List<ProductSearchResponse> getProductByBrand(String brandName);

    Page<ProductSearchResponse> getAllProducts(int page, int size);

    List<ProductSearchResponse> feedByWishProducts();

    List<ProductSearchResponse> searchRequest(
            List<String> categories,
            String brand,
            String sortOrder,
            String sortBy,
            HttpServletRequest httpServletRequest
    );
}
