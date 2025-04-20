package ru.bakht.internetshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.model.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepo extends JpaRepository<Cart, Cart.CartId> {

    Optional<Cart> findByUserIdAndProductId(UUID userId, UUID productId);

    List<Cart> findAllByUser(User user);
}
