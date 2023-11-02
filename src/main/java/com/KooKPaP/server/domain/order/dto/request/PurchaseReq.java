package com.KooKPaP.server.domain.order.dto.request;

import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.Purchase;


public class PurchaseReq {
    private Bucket bucket;
    private String name;
    private Integer count;

    public Purchase from(Bucket bucket) {
        return Purchase.builder()
                .name(this.name)
                .bucket(this.bucket)
                .count(this.count)
                .build();

    }
}
