package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.StockLogModificationDTO;
import com.ECommerceApp.Model.StockLog;
import com.ECommerceApp.Service.StockLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StockLogController { // admin, seller

    @Autowired
    private StockLogService stockLogService;

    // this will insert is the stock is previously not available, updates if already available
    @PostMapping("/updateStock") // seller
    public ResponseEntity<?> insertStock(@Valid @RequestBody StockLogModificationDTO stockLogModificationDTO){
        return ResponseEntity.ok(stockLogService.modifyStock(stockLogModificationDTO));
    }

    @PostMapping("/insertStockLogs")//seller
    public ResponseEntity<?> insertStockLogs(@Valid @RequestBody List<@Valid StockLogModificationDTO> stockLogModificationDTOS){
        List<StockLog> stockLogs = new ArrayList<>();
        for(StockLogModificationDTO stockLogModificationDTO : stockLogModificationDTOS){
            stockLogs.add(stockLogService.modifyStock(stockLogModificationDTO));
        }
        return ResponseEntity.ok(stockLogs);
    }


    @GetMapping("/getStockLogByProduct/{productId}") //seller,admin.
    public StockLog getStockLogByProduct(@PathVariable String productId){
        return stockLogService.getByProductId(productId);
    }



}
