package com.ECommerceApp.ServiceImplementation.Product;

import com.ECommerceApp.DTO.Product.ProductCreationRequest;
import com.ECommerceApp.DTO.Product.ProductSearchResponse;
import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Mappers.ProductMapper;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Repository.Product.ProductRepository;
import com.ECommerceApp.ServiceInterface.Product.IProductService;
import com.ECommerceApp.ServiceInterface.Product.IStockLogService;
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
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IStockLogService stockLogService;
    @Autowired
    private ProductMapper productMapper;

    public Product createProduct(ProductCreationRequest request) {
//        Product product = mapToEntity(request);
        log.info("Creating the new product");
        Product product = productMapper.toProduct(request);
//        BeanUtils.copyProperties(request,product);
        product.setAddedOn(new Date());
        stockLogService.getByProductId(request.getId());
        return saveProduct(product);
    }

    public String  createProductList(List<ProductCreationRequest> products){
        int count = 0;

        for(ProductCreationRequest product : products){
            Product p = productMapper.toProduct(product);
//            BeanUtils.copyProperties(product,p);
            saveProduct(p);
            count++;
        }
        if(count == products.size()){
            return "Products are inserted successfully!  "+count;
        }
        return "Something went wrong";
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        log.info("Getting the product by id: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }

    public Product updateProduct(ProductCreationRequest request) {
        log.info("Updating the product by id: {}", request.getId());
        Product existing = getProductById(request.getId());
        BeanUtils.copyProperties(request,existing);
        return saveProduct(existing);
    }

    public String  deleteProduct(String id) {
        log.warn("Deleting the product with id: {}", id);
        if (!productRepository.existsById(id)) {
            log.warn("There is no product present with id: {}", id);
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        productRepository.deleteById(id);
        log.warn("Deleting the product with id: {}", id);
        return "Product deleted Successfully";
    }


    public double getProductPrice(String id){
        Product product = getProductById(id);
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


    public List<Product> getProductsByCategoryAndBrand(List<String> categoryIds, String brand) {
        return productRepository.findByCategoryIdsContainingAllAndBrandIgnoreCase(categoryIds, brand);
    }


    public List<ProductSearchResponse> getProductsByBrand(String brandName) {
        return productRepository.findByBrandIgnoreCase(brandName);
    }


    public Product saveProduct(Product product){
        return productRepository.save(product);
    }


    public List<Product> getProductsBySellerId(String sellerId) {
        return productRepository.findBySellerId(sellerId);
    }
}
