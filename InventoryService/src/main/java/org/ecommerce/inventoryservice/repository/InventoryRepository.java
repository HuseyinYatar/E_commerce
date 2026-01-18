package org.ecommerce.inventoryservice.repository;

import jakarta.persistence.LockModeType;
import org.ecommerce.inventoryservice.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {


    //prevent the race condition
    @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<Inventory>findByProductIdAndCountGreaterThanEqual(Integer productId, Integer count);
}
