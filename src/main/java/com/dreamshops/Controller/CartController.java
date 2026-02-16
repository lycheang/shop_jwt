package com.dreamshops.Controller;

import com.dreamshops.Response.ApiResponse;
import com.dreamshops.model.Cart;
import com.dreamshops.service.cart.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final ICartService cartService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<ApiResponse> getCartById(@PathVariable Long id){
        try {
            Cart cart = cartService.getCartById(id);
            return ResponseEntity.ok(new ApiResponse("Success", id));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage() ,null));
        }
    }


    @DeleteMapping("/{id}/clear")
    public ResponseEntity<ApiResponse> ClearCart(@PathVariable Long id){
        try {
            cartService.ClearCart(id);
            return ResponseEntity.ok(new ApiResponse("Clear Cart Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage() ,null));
        }
    }


    @GetMapping("/{id}/total-amount")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long id){
        try {
            BigDecimal totalAmount = cartService.getTotalAmount(id);
            return ResponseEntity.ok(new ApiResponse("Total price", totalAmount));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage() ,null));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCarts(){
        try {
            List<Cart> carts = cartService.getAllCarts();
            return ResponseEntity.ok(new ApiResponse("Success", carts));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage() ,null));
        }
    }
}
