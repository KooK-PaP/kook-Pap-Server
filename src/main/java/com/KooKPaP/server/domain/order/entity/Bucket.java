package com.KooKPaP.server.domain.order.entity;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Bucket")
@SQLDelete(sql = "UPDATE menu SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Bucket extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;          // 누가 주문했는지

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;      // 어떤 가게에 주문을 했는지

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private BucketState state;          // 주문 상태 (처리 됐는지 안됐는지.. 등등)

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;         // 주문 총 금액

    @Builder
    public Bucket(Long id, Member member, Restaurant restaurant, BucketState state, Integer totalPrice) {
        this.id = id;
        this.member = member;
        this.restaurant = restaurant;
        this.state = state;
        this.totalPrice = totalPrice;
    }
}
