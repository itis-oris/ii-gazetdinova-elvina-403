package ru.isgaij.smartcloset.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "items")
@Getter
@Setter
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String color;

    @Column(length = 20)
    private String size;

    @Column(length = 20)
    private String season;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column(name = "color_name", length = 100)
    private String colorName;

    @Column(name = "complement_color", length = 20)
    private String complementColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_tags",
            joinColumns        = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
