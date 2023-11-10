package com.KooKPaP.server.domain.order.dto.response;

import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.Purchase;


public class PurchaseRes {
    private Bucket bucket;
    private String name;
    private Integer count;

    public static PurchaseRes of(Purchase purchase) {
        PurchaseRes purchaseRes = new PurchaseRes();
        purchaseRes.bucket = purchase.getBucket();
        purchaseRes.count = purchase.getCount();
        purchaseRes.name = purchase.getName();
        return purchaseRes;
    }
}
