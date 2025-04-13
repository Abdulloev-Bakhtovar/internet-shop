package ru.bakht.internetshop.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import ru.bakht.internetshop.auth.model.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"millimeter", "megapixel", "type", "product", "productInfoImages"})
@EqualsAndHashCode(exclude = {"millimeter", "megapixel", "type", "product", "productInfoImages"}, callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_infos")
public class ProductInfo extends BaseEntity {

    String name;
    Instant updateDate;
    BigDecimal price;
    BigDecimal discount;
    Boolean isInterestDiscount;
    String description;

    @ManyToOne
    @JoinColumn(name = "millimeter_id", nullable = false)
    Millimeter millimeter;

    @ManyToOne
    @JoinColumn(name = "megapixel_id", nullable = false)
    Megapixel megapixel;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    Type type;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @OneToMany(mappedBy = "productInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("number ASC")
    List<ProductInfoImage> productInfoImages;
}
