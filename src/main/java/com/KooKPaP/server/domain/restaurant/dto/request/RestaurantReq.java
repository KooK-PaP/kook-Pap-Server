package com.KooKPaP.server.domain.restaurant.dto.request;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
public class RestaurantReq {

    @NotEmpty(message = "가게 이름이 공란입니다.")
    private String name;

    private String introduction;

    private String callNumber;

    private String address;

    @NotNull
    private OperationReq operation;

    public Restaurant from(Member member) {
        return Restaurant.builder()
                .name(this.name)
                .member(member)
                .introduction(this.introduction)
                .callNumber(this.callNumber)
                .address(this.address)
                .build();

    }
}
