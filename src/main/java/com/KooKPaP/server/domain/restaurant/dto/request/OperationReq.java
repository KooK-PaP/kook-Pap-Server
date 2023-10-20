package com.KooKPaP.server.domain.restaurant.dto.request;

import com.KooKPaP.server.domain.restaurant.entity.Operation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
public class OperationReq {

    private LocalTime monOpen;

    private LocalTime monClose;

    private LocalTime tueOpen;

    private LocalTime tueClose;

    private LocalTime wedOpen;

    private LocalTime wedClose;

    private LocalTime thuOpen;

    private LocalTime thuClose;

    private LocalTime friOpen;

    private LocalTime friClose;

    private LocalTime satOpen;

    private LocalTime satClose;

    private LocalTime sunOpen;

    private LocalTime sunClose;

    public Operation from() {
        return Operation.builder()
                .monOpen(this.monOpen)
                .monClose(this.monClose)
                .tueOpen(this.tueOpen)
                .tueClose(this.tueClose)
                .wedOpen(this.wedOpen)
                .wedClose(this.wedClose)
                .thuOpen(this.thuOpen)
                .thuClose(this.thuClose)
                .friOpen(this.friOpen)
                .friClose(this.friClose)
                .satOpen(this.satOpen)
                .satClose(this.satClose)
                .sunOpen(this.sunOpen)
                .sunClose(this.sunClose)
                .build();
    }
}
