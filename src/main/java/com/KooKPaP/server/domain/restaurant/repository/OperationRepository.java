package com.KooKPaP.server.domain.restaurant.repository;

import com.KooKPaP.server.domain.restaurant.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
}
