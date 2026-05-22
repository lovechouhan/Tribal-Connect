package com.eCommerce.Ecommerce.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eCommerce.Ecommerce.Entities.Tip;

@Repository
public interface TipRepo extends JpaRepository<Tip, Long> {

    List<Tip> findByProductId(Long productId);

    List<Tip> findByArtisanId(Long artisanId);

    List<Tip> findByUserId(Long userId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Tip t WHERE t.artisan.id = ?1")
    int getTotalTipAmountByArtisanId(Long artisanId);

    @Query("SELECT COUNT(DISTINCT t.user.id) FROM Tip t WHERE t.artisan.id = ?1")
    int getUniqueSupportersByArtisanId(Long artisanId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Tip t WHERE t.product.id = ?1")
    int getTotalTipAmountByProductId(Long productId);

    @Query("SELECT COUNT(t) FROM Tip t WHERE t.product.id = ?1")
    int getTipCountByProductId(Long productId);
}
