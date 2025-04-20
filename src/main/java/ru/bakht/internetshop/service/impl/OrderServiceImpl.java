package ru.bakht.internetshop.service.impl;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bakht.internetshop.auth.mapper.OrderMapper;
import ru.bakht.internetshop.auth.mapper.OrderProductInfoMapper;
import ru.bakht.internetshop.auth.mapper.UserInfoMapper;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.enums.EmailTemplateName;
import ru.bakht.internetshop.auth.repository.UserInfoRepo;
import ru.bakht.internetshop.auth.service.EmailService;
import ru.bakht.internetshop.auth.util.AuthUtils;
import ru.bakht.internetshop.exception.AppException;
import ru.bakht.internetshop.model.Cart;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.OrderProductInfo;
import ru.bakht.internetshop.model.Product;
import ru.bakht.internetshop.model.dto.OrderDto;
import ru.bakht.internetshop.model.dto.OrderFilterDto;
import ru.bakht.internetshop.model.dto.ProductDto;
import ru.bakht.internetshop.repository.CartRepo;
import ru.bakht.internetshop.repository.OrderProductInfoRepo;
import ru.bakht.internetshop.repository.OrderRepo;
import ru.bakht.internetshop.repository.ProductInfoRepo;
import ru.bakht.internetshop.service.OrderService;
import ru.bakht.internetshop.specification.OrderSpecifications;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Value("${application.email.order.notification.username}")
    private String orderNotificationEmail;

    private final UserInfoRepo userInfoRepository;
    private final OrderRepo orderRepository;
    private final CartRepo cartRepository;
    private final OrderProductInfoRepo orderProductInfoRepo;
    private final EmailService emailService;
    private final OrderMapper orderMapper;
    private final UserInfoMapper userInfoMapper;
    private final OrderProductInfoMapper orderProductInfoMapper;
    private final AuthUtils authUtils;
    private final ProductInfoRepo productInfoRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getUserOrders(OrderFilterDto criteria, int page, int size, String sortField, String sortDirection) {

        var user = authUtils.getAuthenticatedUser();

        if (user == null) {
            log.error("User is not authenticated");
            throw new AppException("User is not authenticated", HttpStatus.UNAUTHORIZED);
        }

        log.info("Attempting to retrieve orders for user: {}", user.getEmail());

        Pageable pageable = createPageRequest(page, size, sortField, sortDirection);

        Specification<Order> spec = OrderSpecifications.withFilter(criteria)
                .and((root, query, cb) -> cb.equal(root.get("user").get("id"), user.getId()));

        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto> getAllOrders(OrderFilterDto criteria, int page, int size, String sortField, String sortDirection) {
        log.info("Attempting to retrieve all orders based on provided criteria");

        Pageable pageable = createPageRequest(page, size, sortField, sortDirection);
        Specification<Order> spec = OrderSpecifications.withFilter(criteria);

        return orderRepository.findAll(spec, pageable)
                .map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getOrdersByIds(List<UUID> orderIds) {
        log.info("Attempting to retrieve orders for the given list of order IDs");

        List<Order> orders = orderRepository.findAllByIdInOrderByOrderDateDesc(orderIds);

        if (orders.isEmpty()) {
            log.warn("No orders found for the given list of IDs");
            return List.of();
        }

        return orders.stream()
                .filter(order -> order.getUser() == null)
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UUID createOrder(OrderDto orderDto) {
        log.info("Attempting to create a new order for user: {}", orderDto.getUserInfo().getEmail());

        var user = authUtils.getAuthenticatedUser();
        var userInfo = userInfoMapper.toEntity(orderDto.getUserInfo());
        userInfo = userInfoRepository.save(userInfo);

        var order = orderMapper.toEntity(orderDto);
        order.setUserInfo(userInfo);

        if (user != null) {
            order.setUser(user);
        }

        order = orderRepository.save(order);

        List<OrderProductInfo> orderProductInfos = orderProductInfoMapper.toEntityList(orderDto.getProducts(), order);

        orderProductInfoRepo.saveAll(orderProductInfos);

        if (user != null) {
            clearUserCart(user, orderDto.getProducts());
        }

        try {
            emailService.sendEmail(
                    orderNotificationEmail,
                    EmailTemplateName.ORDER_NOTIFICATION,
                    null,
                    "Новый заказ: " + order.getId(),
                    orderProductInfos
            );
        } catch (MessagingException e) {
            log.error("Error sending notify about order creation: {}", e.getMessage());
        }

        return order.getId();
    }

    private void clearUserCart(User user, List<ProductDto> products) {

        List<Product> productList = products.stream()
                .map(productDto -> {
                    var productInfo = productInfoRepository.findById(productDto.getProductInfo().getId())
                            .orElse(null);

                    if (productInfo != null) {
                        return productInfo.getProduct();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();

        List<Cart> userCart = cartRepository.findAllByUser(user);

        for (var cartItem : userCart) {
            for (var product : productList) {
                if (cartItem.getProduct().getId().equals(product.getId())) {
                    cartRepository.delete(cartItem);
                    log.info("Product with ID: {} removed from cart for user: {}", product.getId(), user.getEmail());
                    break;
                }
            }
        }
    }

    private Pageable createPageRequest(int page, int size, String sortField, String sortDirection) {
        Sort.Direction direction = Sort.Direction.fromOptionalString(sortDirection).orElse(Sort.Direction.DESC);
        return PageRequest.of(page, size, Sort.by(direction, sortField));
    }

}
