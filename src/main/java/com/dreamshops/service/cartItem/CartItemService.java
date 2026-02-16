package com.dreamshops.service.cartItem;

import com.dreamshops.model.Cart;
import com.dreamshops.model.CartItem;
import com.dreamshops.model.Product;
import com.dreamshops.service.cart.CartRepository;
import com.dreamshops.service.cart.ICartService;
import com.dreamshops.service.product.IProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static java.util.Arrays.stream;
import static java.util.Locale.filter;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;

    @Transactional
    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1. Get the cart
        //2. Get the product
        //3. Check if the product already in the cart
        //4. If Yes, then increase the quantity with the requested quantity
        //5. If No, then initiate a new CartItem entry.
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);
        if (product.getInventory() == 0) {
            throw new RuntimeException("Product is Out of Stock!");
        }

        // 2. Check if user requests more than we have
        if (product.getInventory() < quantity) {
            throw new RuntimeException("Not enough items in stock. Only " + product.getInventory() + " left.");
        }
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if (cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        }
        else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    // ... other methods ...



    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getItems()
                .stream().map(CartItem ::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
    Cart cart =cartService.getCartById(cartId);
    CartItem cartToRemove=getCartItemById(cartId,productId);
        cart.removeItem(cartToRemove);
        cartRepository.save(cart);

    }
    @Override
    public CartItem getCartItemById(Long id, Long productId){
        Cart cart =cartService.getCartById(id);
        return cart.getItems()
                .stream()
                .filter(item->item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(()->new RuntimeException("Item not found"));
    }
}
