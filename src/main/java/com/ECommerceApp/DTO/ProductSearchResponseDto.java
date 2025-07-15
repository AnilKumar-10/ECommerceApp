package com.ECommerceApp.DTO;

import lombok.Data;

import java.util.List;

@Data
public class ProductSearchResponseDto {

    private String Name;
    private String description;
    private double price;
    private String brand;
    private String returnPolicy;
    private List<String> colors;
    private List<String> sizes;
    private List<String> images;
    private double rating;
    private boolean isAvailable;

}
