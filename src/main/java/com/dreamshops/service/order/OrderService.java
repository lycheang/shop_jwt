package com.dreamshops.service.order;

import com.dreamshops.dto.OrderDto;
import com.dreamshops.enums.OrderStatus;
import com.dreamshops.model.Cart;
import com.dreamshops.model.Order;
import com.dreamshops.model.Orderitem;
import com.dreamshops.model.Product;
import com.dreamshops.service.cart.CartService;
import com.dreamshops.service.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor

public class OrderService implements IOrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order =createOrder(cart);
        List<Orderitem> orderItemsList = createOrderItems(order,cart);

        order.setOrderItems(new HashSet<>(orderItemsList));
        order.setTotalAmount(calculateTotalAmount(orderItemsList));
        Order savedOrder = orderRepository.save(order);

        cartService.ClearCart(cart.getId());

    return savedOrder;
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        //set the user
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    private List<Orderitem> createOrderItems(Order order, Cart cart){
        return cart.getItems().stream().map(cartItem -> {
            Product product = cartItem.getProduct();
            product.setInventory(product.getInventory() - cartItem.getQuantity());
            productRepository.save(product);
            return new Orderitem(
                                order,
                                product,
                    cartItem.getUnitPrice(),
                    cartItem.getQuantity());
        }).toList();
    }

    @Override
    public OrderDto getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::convertToDto)
                .orElseThrow(()->new RuntimeException("Order with id " + orderId + " not found"));
    }

    private BigDecimal calculateTotalAmount(List<Orderitem> orderitemList) {
        return orderitemList
                .stream()
                .map(item->item.getUnitPrice()
                        .multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

    public List<OrderDto> getUserOrders(Long userId){
        List<Order> orders=orderRepository.findByUserId(userId);
        return orders.stream().map(this::convertToDto).toList();
    }

    @Override
    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
        cartService.ClearCart(orderId);
    }
    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }
}
