package com.KooKPaP.server.domain.order.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "purchase")
@SQLDelete(sql = "UPDATE menu SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Purchase {
    // 음식 주문 내용에 해당하는 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bucket_id")
    private Bucket bucket;      // bucket은 장바구니의 역할을 수행

    @Column(name = "name", nullable = false)
    private String name;        // 주문 음식 이름

    @Column(name = "price", nullable = false)
    private Integer price;      // 주문 음식 가격

    @Column(name = "count", nullable = false)
    private Integer count;      // 주문 음식 수량

    @Builder
    public Purchase(Long id, Bucket bucket, String name, Integer price, Integer count) {
        this.id = id;
        this.bucket = bucket;
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
