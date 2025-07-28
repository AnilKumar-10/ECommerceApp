package com.ECommerceApp.ServiceInterface;

import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Model.Product.StockLog;



public interface IStockLogService {

    StockLog modifyStock(StockLogModificationRequest modification);

    StockLog getByProductId(String productId);
}

