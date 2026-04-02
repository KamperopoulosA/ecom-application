package com.app.ecom_application.services;

import com.app.ecom_application.dtos.OrderItemDTO;
import com.app.ecom_application.dtos.OrderResponse;
import com.app.ecom_application.models.CartItem;
import com.app.ecom_application.models.Order;
import com.app.ecom_application.models.OrderItem;
import com.app.ecom_application.models.User;
import com.app.ecom_application.repositories.OrderRepository;
import com.app.ecom_application.repositories.UserRepository;
import com.app.ecom_application.roles.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    public Optional <OrderResponse> createOrder(String userId) {


        //Validate for cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }
        //Validate for user
        Optional<User> userOptional = userRepository.findById(Long.valueOf(userId));
        if(userOptional.isEmpty()){
            return Optional.empty();
        }
        User user = userOptional.get();
        //Calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(CartItem::getPrice)
                .reduce(BigDecimal.ZERO , BigDecimal::add);
        //Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> new OrderItem(
                        null,
                        item.getProduct(),
                        item.getQuantity(),
                        item.getPrice(),
                        order
                ))
                .toList();
        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);
        //Clear the cart
        cartService.clearCart(userId);

        return Optional.of(mapToOrderResponse(savedOrder));
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return new OrderResponse(
               order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getItems().stream()
                        .map(orderItem-> new OrderItemDTO(
                                orderItem.getId(),
                                orderItem.getProduct().getId(),
                                orderItem.getQuantity(),
                                orderItem.getPrice(),
                                orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))
                )).toList(),
                order.getCreatedAt()
        );
    }
}
