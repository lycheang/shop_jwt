package com.dreamshops.service.cart;

import com.dreamshops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dreamshops.model.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long>{

    List<Cart> user(User user);

    Cart findByUserId(Long userId);
}
