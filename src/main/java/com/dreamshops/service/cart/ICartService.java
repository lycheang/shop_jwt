package com.dreamshops.service.cart;

import com.dreamshops.model.Cart;
import com.dreamshops.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface ICartService {
    List<Cart> getAllCarts();
    Cart getCartById(Long id);
    void ClearCart(Long id);
    BigDecimal getTotalAmount(Long id);

    Cart initializeNewCart(User user);

    Cart getCartByUserId(Long userId);
}
