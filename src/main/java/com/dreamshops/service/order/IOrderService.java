package com.dreamshops.service.order;

import com.dreamshops.dto.OrderDto;
import com.dreamshops.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrder(Long orderId);
    List<OrderDto> getUserOrders(Long userId);
    void deleteOrder(Long orderId);

    OrderDto convertToDto(Order order);
}
