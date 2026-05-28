package ru.isgaij.smartcloset.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "wishlist_items")
@Getter
@Setter
@NoArgsConstructor
public class WishlistItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(length = 500)
    private String url;

    @Column(length = 500)
    private String note;

    @Column(name = "added_date")
    private LocalDate addedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @PrePersist
    void onCreate() {
        if (addedDate == null) {
            addedDate = LocalDate.now();
        }
    }
}
