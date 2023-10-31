package com.KooKPaP.server.domain.restaurant.dto.response;

import com.KooKPaP.server.domain.restaurant.entity.Operation;
import lombok.Getter;

import java.time.LocalTime;

@Getter
public class OperationRes {

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

    public static OperationRes of(Operation operation) {
        OperationRes operationRes = new OperationRes();
        operationRes.monOpen = operation.getMonOpen();
        operationRes.monClose = operation.getMonClose();
        operationRes.tueOpen = operation.getTueOpen();
        operationRes.tueClose = operation.getTueClose();
        operationRes.wedOpen = operation.getWedOpen();
        operationRes.wedClose = operation.getWedClose();
        operationRes.thuOpen = operation.getThuOpen();
        operationRes.thuClose = operation.getThuClose();
        operationRes.friOpen = operation.getFriOpen();
        operationRes.friClose = operation.getFriClose();
        operationRes.satOpen = operation.getSatOpen();
        operationRes.satClose = operation.getSatClose();
        operationRes.sunOpen = operation.getSunOpen();
        operationRes.sunClose = operation.getSunClose();

        return operationRes;
    }
}
