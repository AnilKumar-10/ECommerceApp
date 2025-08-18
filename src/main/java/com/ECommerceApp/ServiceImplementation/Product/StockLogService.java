package com.ECommerceApp.ServiceImplementation.Product;

import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Exceptions.Product.ProductNotFoundException;
import com.ECommerceApp.Model.Product.Product;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.Model.Product.StockLogModification;
import com.ECommerceApp.Repository.Product.ProductRepository;
import com.ECommerceApp.Repository.Product.StockLogRepository;
import com.ECommerceApp.ServiceInterface.User.IEmailService;
import com.ECommerceApp.ServiceInterface.Product.IStockLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
@Slf4j
@Service
public class StockLogService implements IStockLogService {
    @Autowired
    private StockLogRepository stockLogRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private IEmailService emailService;

    public StockLog modifyStock(StockLogModificationRequest modification) {
        // 1. Find existing stock log or create a new one
        log.info("updating the stock of : {}", modification.getProductId());
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
        int newQuantity = getQuantity(modification, stockLog);
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
        if(newQuantity <= 10){
            // this will send the notification to the seller when Product Stock is Low.
            emailService.sendLowStockAlertToSeller("iamanil3121@gmail.com",updatedLog);
        }

        return updatedLog;
    }

    private int getQuantity(StockLogModificationRequest modification, StockLog stockLog) {
        int change = modification.getQuantityChanged();

        StockLogModification.ActionType action = modification.getAction();

        if (action == StockLogModification.ActionType.ADD
                || action == StockLogModification.ActionType.RETURNED
                || action == StockLogModification.ActionType.CANCELLED) {
            change = Math.abs(change); // Ensure positive change
        } else if (action == StockLogModification.ActionType.SOLD) {
            change = -Math.abs(change); // Ensure negative change
        } else {
            throw new IllegalArgumentException("Invalid stock action: " + action);
        }

        // 3. Apply change
        return stockLog.getCurrentQuantity() + change;
    }


    
    private StockLogModification getModificationLog(StockLogModificationRequest modification) {

        StockLogModification modificationLog = new StockLogModification();
        BeanUtils.copyProperties(modification,modificationLog);
        return modificationLog;
    }

    public StockLog getByProductId(String productId){
        return stockLogRepository.findByProductId(productId);
    }


}
