package com.KooKPaP.server.domain.restaurant.controller;

import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.service.RestaurantService;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Secured("ROLE_ADMIN")
    @PostMapping("/v{version}/restaurant/register")
    public ApplicationResponse<RestaurantRes> register(@PathVariable("version") Long version, @AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody RestaurantReq restaurantReq) {
        Long memberId = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_CREATED, restaurantService.register(version, memberId, restaurantReq));
    }

    @PutMapping("/v{version}/restaurant/update/{restaurant_id}")
    public ApplicationResponse<RestaurantRes> update(@PathVariable("version") Long version, @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("restaurant_id") Long restaurantId, @Valid @RequestBody RestaurantReq restaurantReq) {
        Long memberId = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.update(version, memberId, restaurantId, restaurantReq));
    }

    @DeleteMapping("/v{version}/restaurant/delete/{restaurant_id}")
    public ApplicationResponse<Void> delete(@PathVariable("version") Long version, @AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("restaurant_id") Long restaurantId) {
        Long memberId = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.delete(memberId, restaurantId));
    }

    @GetMapping("/v{version}/restaurant/{restaurant_id}")
    public ApplicationResponse<RestaurantRes> getOne(@PathVariable("version") Long version, @PathVariable("restaurant_id") Long restaurantId) {
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.getOne(restaurantId));
    }

    @GetMapping("/v{version}/restaurant/my")
    public ApplicationResponse<List<RestaurantRes>> getMy(@PathVariable("version") Long version, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long memberId = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.getMy(memberId));
    }

    @GetMapping("/v{version}/restaurant")
    public ApplicationResponse<Slice<RestaurantRes>> getAll(@PathVariable("version") Long version, @RequestParam(name = "cursor", required = false) Long cursor, @PageableDefault(size = 15, sort = "created_at DESC")Pageable pageable) {
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, restaurantService.getAll(cursor, pageable));
    }
}
