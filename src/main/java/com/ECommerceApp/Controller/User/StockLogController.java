package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.ServiceInterface.IStockLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StockLogController { // admin, seller

    @Autowired
    private IStockLogService stockLogService;

    // this will insert is the stock is previously not available, updates if already available
    @PostMapping("/updateStock") // seller
    public ResponseEntity<?> insertStock(@Valid @RequestBody StockLogModificationRequest stockLogModificationDTO){
        return ResponseEntity.ok(stockLogService.modifyStock(stockLogModificationDTO));
    }


    @PostMapping("/insertStockLogs")//seller
    public ResponseEntity<?> insertStockLogs(@Valid @RequestBody List<@Valid StockLogModificationRequest> stockLogModificationDTOS){
        List<StockLog> stockLogs = new ArrayList<>();
        for(StockLogModificationRequest stockLogModificationDTO : stockLogModificationDTOS){
            stockLogs.add(stockLogService.modifyStock(stockLogModificationDTO));
        }
        return ResponseEntity.ok(stockLogs);
    }


    @GetMapping("/getStockLogByProduct/{productId}") //seller,admin.
    public StockLog getStockLogByProduct(@PathVariable String productId){
        return stockLogService.getByProductId(productId);
    }



}
