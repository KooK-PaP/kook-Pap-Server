package com.KooKPaP.server.domain.restaurant.repository;

import com.KooKPaP.server.domain.restaurant.entity.Operation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OperationRepository extends JpaRepository<Operation, Long> {

    // 가게와 연관된 운영시간 정보 찾기 위함. (레스토랑 id와 삭제 여부가 조건절에 포함)
    Optional<Operation> findOperationByRestaurantIdAndDeletedAtIsNull(Long id);
}
