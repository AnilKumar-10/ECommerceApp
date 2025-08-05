package com.ECommerceApp.ServiceInterface.Product;

import com.ECommerceApp.DTO.Product.StockLogModificationRequest;
import com.ECommerceApp.Model.Product.StockLog;



public interface IStockLogService {

    StockLog modifyStock(StockLogModificationRequest modification);

    StockLog getByProductId(String productId);
}

