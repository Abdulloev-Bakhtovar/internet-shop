package ru.bakht.internetshop.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@Table(name = "millimeters")
public class Millimeter extends BaseProductFeatureEntity {

}
