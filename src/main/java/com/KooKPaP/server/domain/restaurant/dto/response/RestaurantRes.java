package com.KooKPaP.server.domain.restaurant.dto.response;

import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class RestaurantRes {

    private Long id;

    private Long memberId; // 가게 주인의 정보를 반환하여, 클라이언트에서 가게 주인인지에 따라 디자인 변화 가능하도록 함.

    private String name;

    private String introduction;

    private String callNumber;

    private String address;

    private OperationRes operation;

    public static RestaurantRes of(Restaurant restaurant) {
        RestaurantRes restaurantRes = new RestaurantRes();
        restaurantRes.id = restaurant.getId();
        restaurantRes.memberId = restaurant.getMember().getId();
        restaurantRes.name = restaurant.getName();
        restaurantRes.introduction = restaurant.getIntroduction();
        restaurantRes.callNumber = restaurant.getCallNumber();
        restaurantRes.address = restaurant.getAddress();
        restaurantRes.operation = OperationRes.of(restaurant.getOperation());

        return restaurantRes;
    }
}
