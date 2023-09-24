package com.KooKPaP.server.domain.restaurant.entity;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Restaurant")
@SQLDelete(sql = "UPDATE restaurant SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "name", nullable = false)
    private String name;        // 가게 이름

    @Column(name = "address", nullable = false)
    private String address;     // 가게 주소

    @Column(name = "call_number", nullable = false)
    private String callNumber;      // 가게 전화번호

    @Column(name = "introduction", nullable = false, columnDefinition = "text")
    private String introduction;        // 가게 설명

    public void update(RestaurantReq restaurantReq) {
        this.name = restaurantReq.getName();
        this.address = restaurantReq.getAddress();
        this.callNumber = restaurantReq.getCallNumber();
        this.introduction = restaurantReq.getIntroduction();
    }

                       @Builder
    public Restaurant(Long id, Member member, String name, String address, String callNumber, String introduction) {
        this.id = id;
        this.member = member;
        this.name = name;
        this.address = address;
        this.callNumber = callNumber;
        this.introduction = introduction;
    }
}
