package com.KooKPaP.server.domain.restaurant.repository;

import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 사용자가 Restaurant의 주인인지 확인하기 위한 코드
    Boolean existsRestaurantByIdAndMemberIdAAndDeletedAtIsNull(Long id, Long memberId);

    // 논리적 삭제가 안 된 Restaurant 정보 반환 코드
    Optional<Restaurant> findRestaurantByIdAndDeletedAtIsNull(Long id);
}
