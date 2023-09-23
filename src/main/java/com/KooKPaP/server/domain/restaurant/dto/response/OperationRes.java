package com.KooKPaP.server.domain.restaurant.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;

@Getter
@RequiredArgsConstructor
public class OperationRes {

    private final LocalTime monOpen;

    private final LocalTime monClose;

    private final LocalTime tueOpen;

    private final LocalTime tueClose;

    private final LocalTime wedOpen;

    private final LocalTime wedClose;

    private final LocalTime thuOpen;

    private final LocalTime thuClose;

    private final LocalTime friOpen;

    private final LocalTime friClose;

    private final LocalTime satOpen;

    private final LocalTime satClose;
}
