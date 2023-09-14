package com.KooKPaP.server.domain.restaurant.entity;

import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Operation")
@SQLDelete(sql = "UPDATE restaurant SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Operation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "mon_open")
    private String monOpen;

    @Column(name = "mon_close")
    private String monClose;

    @Column(name = "tue_open")
    private String tueOpen;

    @Column(name = "tue_close")
    private String tueClose;

    @Column(name = "wed_open")
    private String wedOpen;

    @Column(name = "wed_close")
    private String wedClose;

    @Column(name = "thu_open")
    private String thuOpen;

    @Column(name = "thu_close")
    private String thuClose;

    @Column(name = "fri_open")
    private String friOpen;

    @Column(name = "fri_close")
    private String friClose;

    @Column(name = "sat_open")
    private String satOpen;

    @Column(name = "sat_close")
    private String satClose;

    @Column(name = "sun_open")
    private String sunOpen;

    @Column(name = "sun_close")
    private String sunClose;

    @Builder
    public Operation(Long id, Restaurant restaurant, String monOpen, String monClose, String tueOpen,
                     String tueClose, String wedOpen, String wedClose, String thuOpen, String thuClose,
                     String friOpen, String friClose, String satOpen, String satClose, String sunOpen, String sunClose) {
        this.id = id;
        this.restaurant = restaurant;
        this.monOpen = monOpen;
        this.monClose = monClose;
        this.tueOpen = tueOpen;
        this.tueClose = tueClose;
        this.wedOpen = wedOpen;
        this.wedClose = wedClose;
        this.thuOpen = thuOpen;
        this.thuClose = thuClose;
        this.friOpen = friOpen;
        this.friClose = friClose;
        this.satOpen = satOpen;
        this.satClose = satClose;
        this.sunOpen = sunOpen;
        this.sunClose = sunClose;
    }
}
