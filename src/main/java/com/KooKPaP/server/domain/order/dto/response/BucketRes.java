package com.KooKPaP.server.domain.order.dto.response;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.BucketState;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;



public class BucketRes {

    private Restaurant restaurant;


    private Member member;
    private BucketState state;

    private PurchaseRes purchase;

    public static BucketRes of(Bucket bucket){
        BucketRes bucketRes = new BucketRes();
        bucketRes.member = bucket.getMember();
        bucketRes.restaurant = bucket.getRestaurant();
        bucketRes.state = bucket.getState();
        bucketRes.purchase = PurchaseRes.of(bucket.getPurchase());
        return bucketRes;
    }
}
