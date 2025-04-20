package ru.bakht.internetshop.service;

import ru.bakht.internetshop.model.dto.CartDto;

import java.util.List;

public interface CartService {

    void addOrDeleteProductInCart(CartDto cartDto);
    void addProductsToCart(List<CartDto> cartItems);
}
