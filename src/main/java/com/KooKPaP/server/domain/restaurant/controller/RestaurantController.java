package com.KooKPaP.server.domain.restaurant.controller;

import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.service.RestaurantService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping("/v1/register")
    public ApplicationResponse<RestaurantRes> register(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody RestaurantReq restaurantReq) {
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.register(id, restaurantReq));
    }

    @PutMapping("/v1/update/{restaurant_id}")
    public ApplicationResponse<RestaurantRes> update(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("restaurant_id") Long restaurantId, @Valid @RequestBody RestaurantReq restaurantReq) {
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.update(id, restaurantId, restaurantReq));
    }
}
