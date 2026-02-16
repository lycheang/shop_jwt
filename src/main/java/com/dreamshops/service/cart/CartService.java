package com.dreamshops.service.cart;

import com.dreamshops.model.Cart;
import com.dreamshops.model.User;
import com.dreamshops.service.cartItem.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Arrays.stream;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
//    private final AtomicLong cartIdGenerator=new AtomicLong();

    @Override
    public List<Cart> getAllCarts() {
        return cartRepository.findAll();
    }

    @Override
    public Cart getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Cart with id " + id + " not found"));
        BigDecimal totalAmount=cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
         return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void ClearCart(Long id) {
        Cart cart = getCartById(id);
        cartItemRepository.deleteAllByCartId(id);
        cart.getItems().clear();
        cartRepository.deleteById(id);
    }

    @Override
    public BigDecimal getTotalAmount(Long id) {
        Cart cart = getCartById(id);
        return cart.getTotalAmount();
    }
    @Override
    public Cart initializeNewCart(User user){
        return Optional.ofNullable(cartRepository.findByUserId(user.getId()))
                .orElseGet(()->{
                    Cart newCart=new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId);
    }
}
