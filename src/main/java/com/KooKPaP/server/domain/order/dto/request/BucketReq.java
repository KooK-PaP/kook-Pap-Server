package com.KooKPaP.server.domain.order.dto.request;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.BucketState;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class BucketReq {
    // 필요한거 : 가게 id,
    @NotEmpty(message = "가게가 선택되지 않았습니다")
    private Restaurant restaurant;
    
    @NotEmpty(message = "사용자가 정의되지 않았습니다")
    private Member member;
    private BucketState state = BucketState.AWAITING_ACCEPT; //처음에는 접수 대기중으로 처리

    @NotNull
    private PurchaseReq purchaseReq;

    public Bucket from(Member member) {
        return Bucket.builder()
                .member(member)
                .state(this.state)
                .restaurant(this.restaurant)
                .build();

    }


}
