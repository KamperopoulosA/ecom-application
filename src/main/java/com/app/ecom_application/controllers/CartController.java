package com.app.ecom_application.controllers;

import com.app.ecom_application.dtos.CartItemRequest;
import com.app.ecom_application.models.CartItem;
import com.app.ecom_application.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<String> addToCart(@RequestHeader("X-User-ID") String userId,
                                          @RequestBody CartItemRequest request){
        if(!cartService.addToCart(userId, request)){
            return ResponseEntity.badRequest().body("Product Out of stock or User not found or Product not found");
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Void> removeFromCart(
            @RequestHeader("X-User-ID") String userId,
            @PathVariable Long productId){
       boolean deleted = cartService.deleteItemFromCart(productId,userId);
       return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping()
    public ResponseEntity<List<CartItem>> getCart(@RequestHeader("X-User-ID") String userId){
        return ResponseEntity.ok(cartService.getCart(userId));
    }

}
