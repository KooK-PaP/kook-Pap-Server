package com.KooKPaP.server.domain.restaurant.entity;

import com.KooKPaP.server.domain.restaurant.dto.request.OperationReq;
import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "Operation")
@SQLDelete(sql = "UPDATE restaurant SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Operation extends BaseTimeEntity {
    // 가게가 영업을 시작하는 시간과 영업 종료하는 시간.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "mon_open")
    private LocalTime monOpen;

    @Column(name = "mon_close")
    private LocalTime monClose;

    @Column(name = "tue_open")
    private LocalTime tueOpen;

    @Column(name = "tue_close")
    private LocalTime tueClose;

    @Column(name = "wed_open")
    private LocalTime wedOpen;

    @Column(name = "wed_close")
    private LocalTime wedClose;

    @Column(name = "thu_open")
    private LocalTime thuOpen;

    @Column(name = "thu_close")
    private LocalTime thuClose;

    @Column(name = "fri_open")
    private LocalTime friOpen;

    @Column(name = "fri_close")
    private LocalTime friClose;

    @Column(name = "sat_open")
    private LocalTime satOpen;

    @Column(name = "sat_close")
    private LocalTime satClose;

    @Column(name = "sun_open")
    private LocalTime sunOpen;

    @Column(name = "sun_close")
    private LocalTime sunClose;

    public void update(OperationReq operationReq) {
        this.monOpen = operationReq.getMonOpen();
        this.monClose = operationReq.getMonClose();
        this.tueOpen = operationReq.getTueOpen();
        this.tueClose = operationReq.getTueClose();
        this.wedOpen = operationReq.getWedOpen();
        this.wedClose = operationReq.getWedClose();
        this.thuOpen = operationReq.getThuOpen();
        this.thuClose = operationReq.getThuClose();
        this.friOpen = operationReq.getFriOpen();
        this.friClose = operationReq.getFriClose();
        this.satOpen = operationReq.getSatOpen();
        this.satClose = operationReq.getSatClose();
        this.sunOpen = operationReq.getSunOpen();
        this.sunClose = operationReq.getSunClose();
    }

    @Builder
    public Operation(Long id, Restaurant restaurant, LocalTime monOpen, LocalTime monClose, LocalTime tueOpen,
                     LocalTime tueClose, LocalTime wedOpen, LocalTime wedClose, LocalTime thuOpen, LocalTime thuClose,
                     LocalTime friOpen, LocalTime friClose, LocalTime satOpen, LocalTime satClose, LocalTime sunOpen, LocalTime sunClose) {
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
