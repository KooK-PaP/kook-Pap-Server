package com.KooKPaP.server.domain.restaurant.repository;

import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 사용자가 Restaurant의 주인인지 확인하기 위한 코드
    Boolean existsRestaurantByIdAndMemberId(Long id, Long memberId);
}
