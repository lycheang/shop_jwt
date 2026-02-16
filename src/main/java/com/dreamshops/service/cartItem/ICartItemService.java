package com.dreamshops.service.cartItem;

import com.dreamshops.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, int quantity);
    void updateItemQuantity(Long cartId, Long productId, int quantity);
    void removeItemFromCart(Long cartId, Long productId);

    CartItem getCartItemById(Long id, Long productId);
}
