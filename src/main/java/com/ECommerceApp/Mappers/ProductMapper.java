package com.ECommerceApp.Mappers;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Model.Product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductSearchResponse toProductSearchResponse(Product product);

    Product toProduct(ProductCreationRequest request);
}
