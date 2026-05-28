package ru.isgaij.smartcloset.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.isgaij.smartcloset.entity.Item;
import ru.isgaij.smartcloset.entity.User;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByUser(User user);

    @Query("SELECT i FROM Item i WHERE i.user.id = :userId AND i.season = :season")
    List<Item> findByUserIdAndSeason(@Param("userId") Long userId,
                                    @Param("season") String season);

    @Query("""
           SELECT i FROM Item i
           WHERE i.user.id = :userId
             AND i.price > (SELECT AVG(i2.price)
                            FROM Item i2
                            WHERE i2.user.id = :userId)
           """)
    List<Item> findItemsAboveAveragePrice(@Param("userId") Long userId);

    @Query("""
           SELECT i FROM Item i
           WHERE i.user = :user
             AND LOWER(i.color) = LOWER(:complementColor)
           """)
    List<Item> findByUserAndComplementColor(@Param("user") User user,
                                           @Param("complementColor") String complementColor);

    @Query("""
           SELECT b.name, COUNT(i)
           FROM Item i JOIN i.brand b
           WHERE i.user.id = :userId
           GROUP BY b.name
           ORDER BY COUNT(i) DESC
           """)
    List<Object[]> countItemsPerBrand(@Param("userId") Long userId);

    List<Item> findByUserAndColor(User user, String color);
}
