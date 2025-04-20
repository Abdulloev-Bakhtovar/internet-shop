package ru.bakht.internetshop.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.bakht.internetshop.auth.model.dto.UserInfoDto;
import ru.bakht.internetshop.model.dto.OrderDto;
import ru.bakht.internetshop.model.dto.OrderFilterDto;
import ru.bakht.internetshop.service.OrderService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping
    public ResponseEntity<Page<OrderDto>> getUserOrders(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Instant startDate,
            @RequestParam(required = false) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderDate") String sortField,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        OrderFilterDto criteria = OrderFilterDto.builder()
                .user(UserInfoDto.builder()
                        .name(name)
                        .email(email)
                        .phone(phone)
                        .build())
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return ResponseEntity.ok(orderService.getUserOrders(criteria, page, size, sortField, sortDirection));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<OrderDto>> getAllOrders(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) String email,
                                       @RequestParam(required = false) String phone,
                                       @RequestParam(required = false) Instant startDate,
                                       @RequestParam(required = false) Instant endDate,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @RequestParam(defaultValue = "orderDate") String sortField,
                                       @RequestParam(defaultValue = "DESC") String sortDirection) {

        OrderFilterDto criteria = OrderFilterDto.builder()
                .user(UserInfoDto.builder()
                        .name(name)
                        .email(email)
                        .phone(phone)
                        .build())
                .startDate(startDate)
                .endDate(endDate)
                .build();
        return ResponseEntity.ok(orderService.getAllOrders(criteria, page, size, sortField, sortDirection));
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<OrderDto>> getOrdersByIds(@RequestBody List<UUID> orderIds) {
        return ResponseEntity.ok(orderService.getOrdersByIds(orderIds));
    }

    @PostMapping
    public ResponseEntity<UUID> createOrder(@RequestBody @Valid OrderDto orderDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDto));
    }
}
