package com.KooKPaP.server.domain.order.repository;

import com.KooKPaP.server.domain.order.entity.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
