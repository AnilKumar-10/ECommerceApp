package com.ECommerceApp.Controller.User;

import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Model.Product.StockLog;
import com.ECommerceApp.ServiceInterface.Product.IStockLogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/stockLog")
public class StockLogController { // admin, seller

    @Autowired
    private IStockLogService stockLogService;


    //  SELLER — UPDATE
    @PreAuthorize("hasPermission('STOCK', 'UPDATE')")
    @PostMapping("/updateStock")
    public ResponseEntity<?> insertStock(@Valid @RequestBody StockLogModificationRequest stockLogModificationDTO) {
        return ResponseEntity.ok(stockLogService.modifyStock(stockLogModificationDTO));
    }

    //  SELLER — UPDATE
    @PreAuthorize("hasPermission('STOCK', 'UPDATE')")
    @PostMapping("/insertStockLogs")
    public ResponseEntity<?> insertStockLogs(@Valid @RequestBody List<@Valid StockLogModificationRequest> stockLogModificationDTOS) {
        List<StockLog> stockLogs = new ArrayList<>();
        for (StockLogModificationRequest stockLogModificationDTO : stockLogModificationDTOS) {
            stockLogs.add(stockLogService.modifyStock(stockLogModificationDTO));
        }
        return ResponseEntity.ok(stockLogs);
    }

    //  SELLER, ADMIN — READ
    @PreAuthorize("hasPermission('STOCK', 'READ')")
    @GetMapping("/getStockLogByProduct/{productId}")
    public ResponseEntity<?> getStockLogByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(stockLogService.getByProductId(productId));
    }


    // SELLER INSERT
    @PreAuthorize("hasPermission('STOCK', 'INSERT')")
    @PostMapping("/insertStockLog")
    public ResponseEntity<?> insertStockLogs(@Valid @RequestBody StockLogModificationRequest stockLogModificationDTO) {
        return ResponseEntity.ok(stockLogService.modifyStock(stockLogModificationDTO));
    }

}
