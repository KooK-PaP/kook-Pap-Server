package com.KooKPaP.server.domain.restaurant.repository;

import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    // 사용자가 Restaurant의 주인인지 확인하기 위한 코드
    Optional<Restaurant> findRestaurantByIdAndMemberIdAndDeletedAtIsNull(Long id, Long memberId);

    // 논리적 삭제가 안 된 Restaurant 정보 반환 코드
    Optional<Restaurant> findRestaurantByIdAndDeletedAtIsNull(Long id);

    // MANAGER가 소유하고 있는 가게 리스트 모두 조회
    List<Restaurant> findAllByMemberIdAndDeletedAtIsNull(Long memberId);
}
