package com.KooKPaP.server.domain.order.entity;

public enum BucketState {
    AWAITING_ACCEPT, // 접수 대기
    ACCEPTED,       // 접수 완료
    REFUSED,        // 접수 거절
}
