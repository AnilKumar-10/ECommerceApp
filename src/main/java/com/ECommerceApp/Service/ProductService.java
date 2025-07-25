package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.Review;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
@Slf4j
@Service
public class ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockLogService stockLogService;

    @Autowired
    private ReviewService reviewService;

    public Product createProduct(ProductCreationRequest request) {
//        Product product = mapToEntity(request);
        log.info("Creating the new product");
        Product product = new Product();
        BeanUtils.copyProperties(request,product);
        product.setAddedOn(new Date());
        product.setAvailable(true);
        product.setRating(0.0);
        StockLog stockLog = stockLogService.getByProductId(request.getId());
        return productRepository.save(product);
    }

    public String  createProductList(List<ProductCreationRequest> products){
        int count = 0;

        for(ProductCreationRequest product : products){
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
        log.info("Getting the product by id: "+id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    public Product updateProduct(ProductCreationRequest request) {
        log.info("Updating the product by id: "+request.getId());
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
        log.warn("Deleting the product with id: "+id);
        if (!productRepository.existsById(id)) {
            log.warn("There is no product present with id: "+id);
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        log.warn("Deleting the product with id: "+id);
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

    // return the products that contain any of the given category
    public List<Product> getProductContainsAnyCategory(List<String> allCategoryIds){
        return productRepository.findByCategoryIdsIn(allCategoryIds);
    }


    public List<Product> getProductContainsAllCategory(List<String> allCategoryIds){
        return productRepository.findByCategoryIdsContainingAll(allCategoryIds);
    }


    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    public boolean checkStockAvailability(String productId) {
        return getProductById(productId).isAvailable();

    }


    public long getTotalCountOfProductBySeller(String sellerId) {
        return productRepository.countBySellerId(sellerId);
    }
}
