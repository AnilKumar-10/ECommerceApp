package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.ProductCreationDto;
import com.ECommerceApp.Exceptions.ProductNotFoundException;
import com.ECommerceApp.Model.*;
import com.ECommerceApp.Repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockLogService stockLogService;

    @Autowired
    private ReviewService reviewService;

    public Product createProduct(ProductCreationDto request) {
//        Product product = mapToEntity(request);
        Product product = new Product();
        BeanUtils.copyProperties(request,product);
        product.setAddedOn(new Date());
        product.setAvailable(true);
        product.setRating(0.0);
        StockLog stockLog = stockLogService.getByProductId(request.getId());
        return productRepository.save(product);
    }

    public String  createProductList(List<ProductCreationDto> products){
        int count = 0;

        for(ProductCreationDto product : products){
            Product p = new Product();
            BeanUtils.copyProperties(product,p);
            productRepository.save(p);
            count++;
        }
        if(count==products.size()){
            return "Products are inserted successfully!  "+count;
        }
        return "Something went wrong";
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    public Product updateProduct(ProductCreationDto request) {
        Product existing = getProductById(request.getId());
        BeanUtils.copyProperties(request,existing);
//        existing.setName(request.getName());
//        existing.setDescription(request.getDescription());
//        existing.setPrice(request.getPrice());
//        existing.setStock(request.getStock());
//        existing.setReturnPolicy(request.getReturnPolicy());
//        existing.setCategoryId(request.getCategoryId());
//        existing.setSellerId(request.getSellerId());
//        existing.setColors(request.getColors());
//        existing.setSizes(request.getSizes());
//        existing.setImages(request.getImages());
//        existing.setAvailable(request.isAvailable());
//        existing.setRating(calculateAverageRating(id));
        // here the ratings are not updated by the user/seller because it must update when the new reviews are added to this product
        return productRepository.save(existing);
    }

    public String  deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        return "Product deleted Successfully";
    }

//    private Product mapToEntity(ProductRequest dto) {
//        Product product = new Product();
//        product.setId(dto.getId());
//        product.setName(dto.getName());
//        product.setDescription(dto.getDescription());
//        product.setPrice(dto.getPrice());
//        product.setStock(dto.getStock());
//        product.setReturnPolicy(dto.getReturnPolicy());
//        product.setCategoryIds(dto.getCategoryIds());
//        product.setSellerId(dto.getSellerId());
//        product.setColors(dto.getColors());
//        product.setSizes(dto.getSizes());
//        product.setImages(dto.getImages());
//        return product;
//    }


    public double calculateAverageRating(String productId) {
        List<Review> reviews = reviewService.findByProductId(productId);
        if (reviews.isEmpty()) return 0.0;
        double total = reviews.stream()
                .mapToInt(Review::getRating)
                .sum();
        return total / reviews.size();
    }

    public double getProductPrice(String id){
        Product product = productRepository.findById(id).get();
        return product.getPrice();
    }

    // return the porducts that contain any of the given category
    public List<Product> getProductContainsAnyCategory(List<String> allCategoryIds){
        return productRepository.findByCategoryIdsIn(allCategoryIds);
    }


    public List<Product> getProductContainsAllCategory(List<String> allCategoryIds){
        return productRepository.findByCategoryIdsContainingAll(allCategoryIds);
    }

}
