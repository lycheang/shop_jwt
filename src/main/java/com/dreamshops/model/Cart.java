package com.dreamshops.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal totalAmount= BigDecimal.ZERO;

    @OneToMany(mappedBy = "cart",cascade = CascadeType.ALL,orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<CartItem> items =new HashSet<>();


    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    public void addItem(CartItem item){
        this.items.add(item);
        item.setCart(this);
        UpdateTotalAmount();
    }

    public void removeItem(CartItem item){
        this.items.remove(item);
        item.setCart(null);
        UpdateTotalAmount();
    }

    private void UpdateTotalAmount(){
        this.totalAmount=items.stream().map(item->{
            BigDecimal unitPrice=item.getUnitPrice();
            if(unitPrice==null){
                return BigDecimal.ZERO;
            }
            return unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        }).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

}
