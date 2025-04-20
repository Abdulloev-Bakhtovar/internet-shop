package ru.bakht.internetshop.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.bakht.internetshop.model.Order;
import ru.bakht.internetshop.model.dto.OrderFilterDto;

public class OrderSpecifications {

    public static Specification<Order> withFilter(OrderFilterDto filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            // Фильтрация по имени пользователя
            if (filter.getUser() != null && filter.getUser().getName() != null && !filter.getUser().getName().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("name")),
                                "%" + filter.getUser().getName().toLowerCase() + "%"));
            }

            // Фильтрация по email пользователя
            if (filter.getUser() != null && filter.getUser().getEmail() != null && !filter.getUser().getEmail().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("email")),
                                "%" + filter.getUser().getEmail().toLowerCase() + "%"));
            }

            // Фильтрация по телефону пользователя
            if (filter.getUser() != null && filter.getUser().getPhone() != null && !filter.getUser().getPhone().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("phone")),
                                "%" + filter.getUser().getPhone().toLowerCase() + "%"));
            }

            // Фильтрация по диапазону дат заказа
            if (filter.getStartDate() != null && filter.getEndDate() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.between(root.get("orderDate"), filter.getStartDate(), filter.getEndDate()));
            } else if (filter.getStartDate() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("orderDate"), filter.getStartDate()));
            } else if (filter.getEndDate() != null) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("orderDate"), filter.getEndDate()));
            }

            return predicate;
        };
    }
}
