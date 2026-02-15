package org.ecommerce.inventoryservice.repository;

import jakarta.persistence.LockModeType;
import org.ecommerce.inventoryservice.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItem, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<InventoryItem>findByProductIdAndCountGreaterThanEqual(Integer productId, Integer count);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE InventoryItem i SET i.count = i.count + :quantity WHERE i.productId = :productId")
    void increaseStock(Integer productId, Integer quantity);
}
