package com.KooKPaP.server.domain.restaurant.dto.response;

import com.KooKPaP.server.domain.restaurant.entity.Operation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        operationRes.tueOpen = operation.getMonClose();
        operationRes.tueClose = operation.getMonClose();
        operationRes.wedOpen = operation.getMonClose();
        operationRes.wedClose = operation.getMonClose();
        operationRes.thuOpen = operation.getMonClose();
        operationRes.thuClose = operation.getMonClose();
        operationRes.friOpen = operation.getMonClose();
        operationRes.friClose = operation.getMonClose();
        operationRes.satOpen = operation.getMonClose();
        operationRes.satClose = operation.getMonClose();
        operationRes.sunOpen = operation.getMonClose();
        operationRes.sunClose = operation.getMonClose();

        return operationRes;
    }
}
