package com.ECommerceApp.Controller;

import com.ECommerceApp.DTO.StockLogModificationDTO;
import com.ECommerceApp.Model.StockLog;
import com.ECommerceApp.Service.StockLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StockLogController {

    @Autowired
    private StockLogService stockLogService;


    @PostMapping("/updateStock")
    public StockLog insertStock(@RequestBody StockLogModificationDTO stockLogModificationDTO){
        return stockLogService.modifyStock(stockLogModificationDTO);
    }

    @PostMapping("/insertStockLogs")
    public List<StockLog> insertStockLogs(@RequestBody List<StockLogModificationDTO> stockLogModificationDTOS){
        List<StockLog> stockLogs = new ArrayList<>();
        for(StockLogModificationDTO stockLogModificationDTO : stockLogModificationDTOS){
            stockLogs.add(stockLogService.modifyStock(stockLogModificationDTO));
        }
        return stockLogs;
    }

    @GetMapping("/getStockLogByProduct/{productId}")
    public StockLog getStockLogByProduct(@PathVariable String productId){
        return stockLogService.getByProductId(productId);
    }



}
