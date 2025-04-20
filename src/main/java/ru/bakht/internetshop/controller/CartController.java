package ru.bakht.internetshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bakht.internetshop.model.dto.CartDto;
import ru.bakht.internetshop.service.CartService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER')")
@Tag(name = "Cart", description = "Cart API")
public class CartController {

    private final CartService cartService;

    @PostMapping("/add-product")
    public ResponseEntity<Void> addOrDeleteProductInCart(@RequestBody @Valid CartDto cartDto) {
        cartService.addOrDeleteProductInCart(cartDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/add-products")
    public ResponseEntity<Void> addProductsToCart(@RequestBody List<CartDto> cartItems) {
        cartService.addProductsToCart(cartItems);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

}
