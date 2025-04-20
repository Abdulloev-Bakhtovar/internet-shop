package ru.bakht.internetshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.repository.UserRepo;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.model.Cart;
import ru.bakht.internetshop.model.dto.CartDto;
import ru.bakht.internetshop.repository.CartRepo;
import ru.bakht.internetshop.repository.ProductRepo;
import ru.bakht.internetshop.service.CartService;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final AuthUtils authUtils;

    Cart validateAndGetEntities(String userEmail, UUID productId) {
        log.info("Validating user with email: {} and product with ID: {}", userEmail, productId);

        var user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> createUserNotFoundException(userEmail));

        var product = productRepo.findById(productId)
                .orElseThrow(() -> createProductNotFoundException(productId));

        Cart.CartId cartId = new Cart.CartId(user.getId(), productId);
        return cartRepo.findById(cartId)
                .orElse(new Cart(cartId, 0L, user, product));
    }

    private AppException createUserNotFoundException(String userEmail) {
        log.error("User not found with email: {}", userEmail);
        return new AppException("User not found with email: " + userEmail, HttpStatus.NOT_FOUND);
    }

    private AppException createProductNotFoundException(UUID productId) {
        log.error("Product not found with ID: {}", productId);
        return new AppException("Product not found with ID: " + productId, HttpStatus.NOT_FOUND);
    }

    @Override
    public void addOrDeleteProductInCart(CartDto cartDto) {
        var user = authUtils.getAuthenticatedUser();
        log.info("Processing cart update for user: {} with product ID: {} and quantity: {}",
                user.getEmail(), cartDto.getProductId(), cartDto.getQuantity());

        var cart = validateAndGetEntities(user.getEmail(), cartDto.getProductId());
        processCartUpdate(cart, cartDto.getQuantity());
    }

    @Override
    public void addProductsToCart(List<CartDto> cartItems) {
        var user = authUtils.getAuthenticatedUser();
        String userEmail = user.getEmail();

        for (CartDto cartItem : cartItems) {
            log.info("Processing cart update for user: {} with product ID: {} and quantity: {}",
                    userEmail, cartItem.getProductId(), cartItem.getQuantity());
            var cart = validateAndGetEntities(userEmail, cartItem.getProductId());
            processCartUpdate(cart, cartItem.getQuantity());
        }
    }

    private void processCartUpdate(Cart cart, Long quantity) {
        if (quantity == 0) {
            cartRepo.delete(cart);
            log.info("Successfully deleted product with ID: {} from cart", cart.getId().getProductId());
        } else {
            cart.setQuantity(quantity);
            cartRepo.save(cart);
            log.info("Successfully updated product with ID: {} in cart. New quantity: {}", cart.getId().getProductId(), cart.getQuantity());
        }
    }
}
