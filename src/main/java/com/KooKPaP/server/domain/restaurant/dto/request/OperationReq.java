package com.KooKPaP.server.domain.restaurant.dto.request;

import com.KooKPaP.server.domain.restaurant.entity.Restaurant;

import javax.persistence.*;
import java.time.LocalTime;

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
}
