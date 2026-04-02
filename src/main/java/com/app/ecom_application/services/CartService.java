package com.app.ecom_application.services;

import com.app.ecom_application.dtos.CartItemRequest;
import com.app.ecom_application.models.CartItem;
import com.app.ecom_application.models.Product;
import com.app.ecom_application.models.User;
import com.app.ecom_application.repositories.CartItemRepository;
import com.app.ecom_application.repositories.ProductRepository;
import com.app.ecom_application.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public boolean addToCart(String userId , CartItemRequest request){
        Optional<Product> productOpt = productRepository.findById(request.getProductId());

        if(productOpt.isEmpty())
            return false;


        Product product = productOpt.get();
        if(product.getStockQuantity() < request.getQuantity())
            return false;

        Optional<User> userOpt  = userRepository.findById(Long.valueOf(userId));
        if(userOpt.isEmpty())
            return false;

        User user = userOpt.get();

        CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user , product);
        if(existingCartItem != null){
            //update the quantity
            existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
            existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
            cartItemRepository.save(existingCartItem);
        }else{
            //create new cart item
             CartItem cartItem = new CartItem();
             cartItem.setUser(user);
             cartItem.setProduct(product);
             cartItem.setQuantity(request.getQuantity());
             cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
             cartItemRepository.save(cartItem);
        }
        return true;
    }


    public boolean deleteItemFromCart(Long productId, String userId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        Optional<User> userOpt  = userRepository.findById(Long.valueOf(userId));

        if(productOpt.isPresent() && userOpt.isPresent()){
            cartItemRepository.deleteByUserAndProduct(userOpt.get() , productOpt.get());
            return true;
        }
        return false;
    }

    public List<CartItem> getCart(String userId) {
        return userRepository.findById(Long.valueOf(userId))
                .map(cartItemRepository::findByUser)
                .orElseGet(List::of);


    }

    public void clearCart(String userId) {
        userRepository.findById(Long.valueOf(userId))
                .ifPresent(cartItemRepository::deleteByUser
                );
    }
}
