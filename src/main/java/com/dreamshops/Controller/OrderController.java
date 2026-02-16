package com.dreamshops.Controller;

import com.dreamshops.Response.ApiResponse;
import com.dreamshops.dto.OrderDto;
import com.dreamshops.model.Order;
import com.dreamshops.service.order.IOrderService;
import com.dreamshops.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final IOrderService orderService;
    private final OrderService orderService1;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createOrder(Long userId){
        try {
            Order order = orderService.placeOrder(userId);
            OrderDto orderDto = orderService1.convertToDto(order);
            return ResponseEntity.ok(new ApiResponse("Items Order created successfully", orderDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId){
        try {
            OrderDto order = orderService.getOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Items Order found", order));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> order = orderService.getUserOrders(userId);
            return ResponseEntity.ok(new ApiResponse("Item Order Success!", order));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("Oops!", e.getMessage()));
        }
    }
    @DeleteMapping("/{orderId}")
    public ResponseEntity<ApiResponse> deleteOrder(@PathVariable Long orderId){
        try {
            orderService1.deleteOrder(orderId);
            return ResponseEntity.ok(new ApiResponse("Order Deleted Successfully", null));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Error: " ,e.getMessage()));
        }
    }

}
