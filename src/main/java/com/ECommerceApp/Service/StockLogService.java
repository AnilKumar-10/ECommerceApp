package com.ECommerceApp.Service;

import com.ECommerceApp.DTO.StockLogModificationDTO;
import com.ECommerceApp.Exceptions.ProductNotFoundException;
import com.ECommerceApp.Model.Product;
import com.ECommerceApp.Model.StockLog;
import com.ECommerceApp.Model.StockLogModification;
import com.ECommerceApp.Repository.ProductRepository;
import com.ECommerceApp.Repository.StockLogRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;

@Service
public class StockLogService {
    @Autowired
    private StockLogRepository stockLogRepository;
    @Autowired
    private ProductRepository productRepository;

    public StockLog modifyStock(StockLogModificationDTO modification) {
        // 1. Find existing stock log or create a new one
        StockLog stockLog = stockLogRepository.findByProductIdAndSellerId(modification.getProductId(), modification.getSellerId())
                .orElseGet(() -> {
                    StockLog newLog = new StockLog();
                    newLog.setProductId(modification.getProductId());
                    newLog.setSellerId(modification.getSellerId());
                    newLog.setLogModification(new ArrayList<>());
                    newLog.setCurrentQuantity(0); // default
                    newLog.setTimestamp(new Date());
                    return newLog;
                });

        // 2. Update quantity based on action
        int change = modification.getQuantityChanged();
        String action = modification.getAction().toUpperCase();

        if (action.equals("ADD") || action.equals("RETURNED")) {
            change = Math.abs(change); // Ensure positive change
        } else if (action.equals("SOLD")) {
            change = -Math.abs(change); // Ensure negative change
        } else {
            throw new IllegalArgumentException("Invalid stock action: " + action);
        }

        // 3. Apply change
        int newQuantity = stockLog.getCurrentQuantity() + change;
        stockLog.setCurrentQuantity(newQuantity);
        stockLog.setTimestamp(new Date());

        // 4. Append log entry
        modification.setModifiedAt(new Date());
        stockLog.getLogModification().add(getModificationLog(modification));
        StockLog updatedLog = stockLogRepository.save(stockLog);

        // 5. Update product stock as well
        Product product = productRepository.findById(modification.getProductId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        product.setStock(newQuantity);
        product.setAvailable(newQuantity > 0);
        productRepository.save(product);
        return updatedLog;
    }

    private StockLogModification getModificationLog(StockLogModificationDTO modification) {
        StockLogModification modificationLog = new StockLogModification();
        BeanUtils.copyProperties(modification,modificationLog);
        return modificationLog;
    }

    public StockLog getByProductId(String productId){
        return stockLogRepository.findByProductId(productId);
    }


}
