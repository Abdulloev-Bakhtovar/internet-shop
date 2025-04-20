package ru.bakht.internetshop.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.bakht.internetshop.auth.model.User;
import ru.bakht.internetshop.auth.model.dto.UserDto;

public class UserInfoSpecification {

    public static Specification<User> withFilter(UserDto filter) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();

            if (filter.getEmail() != null && !filter.getEmail().isEmpty()) {
                predicate = criteriaBuilder.and(predicate,
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                                "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getUserInfo() != null) {
                if (filter.getUserInfo().getName() != null && !filter.getUserInfo().getName().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("name")),
                                    "%" + filter.getUserInfo().getName().toLowerCase() + "%"));
                }
                if (filter.getUserInfo().getEmail() != null && !filter.getUserInfo().getEmail().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("email")),
                                    "%" + filter.getUserInfo().getEmail().toLowerCase() + "%"));
                }
                if (filter.getUserInfo().getPhone() != null && !filter.getUserInfo().getPhone().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("phone")),
                                    "%" + filter.getUserInfo().getPhone().toLowerCase() + "%"));
                }
                if (filter.getUserInfo().getAddress() != null && !filter.getUserInfo().getAddress().isEmpty()) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("userInfo").get("address")),
                                    "%" + filter.getUserInfo().getAddress().toLowerCase() + "%"));
                }
            }

            return predicate;
        };
    }
}
