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
    @Query(value = "select r.id, r.member_id, r.name, r.address, r.call_number, r.introduction, o.id, o.restaurant_id, o.mon_open" +
            "from restaurant r " +
            "left join operation o on r.id = o.restaurant_id" +
            "where r.deleted_at is null",
            nativeQuery = true)
    Optional<List<Object[]>> findRestaurantWithOperationByMemberId(Long id);
}
