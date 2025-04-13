package ru.bakht.internetshop.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"productInfo", "image"})
@EqualsAndHashCode(exclude = {"productInfo", "image"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_infos_images")
public class ProductInfoImage {

    @EmbeddedId
    ProductInfoImageId id;

    long number;

    @MapsId("productInfoId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_info_id")
    ProductInfo productInfo;

    @MapsId("imageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    Image image;

    @Getter
    @Setter
    @Embeddable
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class ProductInfoImageId implements Serializable {

        UUID productInfoId;
        UUID imageId;
    }
}
