package com.ECommerceApp.Model.Product;

import lombok.Data;

import java.util.Date;

    @Data
    public class StockLogModification {

        private String userId;
        private ActionType action; // Replaced String with enum
        private int quantityChanged;
        private Date modifiedAt;

        public enum ActionType {
            ADD,
            SOLD,
            RETURNED,
            CANCELLED
        }
    }

