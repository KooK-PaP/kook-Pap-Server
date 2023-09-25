package com.KooKPaP.server.domain.order.controller;

import com.KooKPaP.server.domain.order.OrderService.OrderService;
import com.KooKPaP.server.domain.order.dto.request.OrderReq;
import com.KooKPaP.server.domain.order.dto.response.OrderRes;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    // consumer
    @PostMapping("/consumer")
    public ApplicationResponse<OrderRes> order(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody OrderReq orderReq){
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, orderService.order(id, orderReq));
    }

//    @PostMapping("/cancel/{bucket_id}")
//    public ApplicationResponse<?> cancel(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody CancelReq cancelReq){
//
//    }
//
//    @GetMapping("/orders")
//    public ApplicationResponse<?> myOrders(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody ConsumeReq consumeReq){
//
//    }
//
//    @GetMapping("/{order_id}")
//    public ApplicationResponse<?> myOrdersDetail(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody ConsumeReq consumeReq){
//
//    }
//
//    // Manager
//    @GetMapping("/restaurant")
//    public ApplicationResponse<?> check(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody ConsumeReq consumeReq){
//
//    }
//
//    @PostMapping("/accept/{bucket_id}")
//    public ApplicationResponse<?> receive(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody ConsumeReq consumeReq){
//
//    }
}
