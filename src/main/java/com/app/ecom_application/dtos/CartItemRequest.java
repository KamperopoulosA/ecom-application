package com.app.ecom_application.dtos;

import lombok.Data;

@Data
public class CartItemRequest {
    private Long productId;
    private Integer quantity;

}
