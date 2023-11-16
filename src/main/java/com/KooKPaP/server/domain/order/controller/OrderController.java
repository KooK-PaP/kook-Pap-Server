package com.KooKPaP.server.domain.order.controller;

import com.KooKPaP.server.domain.order.OrderService.OrderService;
import com.KooKPaP.server.domain.order.dto.request.BucketReq;
import com.KooKPaP.server.domain.order.dto.response.BucketRes;
import com.KooKPaP.server.global.common.dto.ApplicationResponse;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import com.KooKPaP.server.global.jwt.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    // consumer

    // 주문하기 
    // 결제부분 좀더 생각
    @PostMapping("/consumer")
    public ApplicationResponse<BucketRes> order(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody BucketReq bucketReq){
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, orderService.order(id, bucketReq));
    }

    //주문 취소하기
    @PostMapping("/consumer/cancel/{bucket_id}")
    public ApplicationResponse<?> cancel(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("bucket_id") Long bucketId){
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, orderService.cancelOrder(id, bucketId));
    }


    //자기 주문 내역 보기
    // state가 accepted인 것만 리스트에 담아서 보여주면 될듯?
    @GetMapping("/consumer/orders")
    public ApplicationResponse<?> myOrders(@AuthenticationPrincipal PrincipalDetails principalDetails, @Valid @RequestBody BucketReq bucketReq){
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, orderService.myOrders(id, bucketReq));
    }



    //현재 주문 확인하기
    //주문 한 거 상세내역 확인할 수 있게
    // dto 설정하기
    @GetMapping("/consumer/detail/{bucket_id}")
    public ApplicationResponse<?> myOrdersDetail(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("bucket_id") Long bucketId){
        Long id = principalDetails.getMember().getId();
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK, orderService.myOrdersDetail(id, bucketId));
    }




    //사장님

    // 가게 사장님이 들어온 주문을 확인하고 취소할건지 확정시킬건지 선택하도록 해야함
    // 주문 들어온 버켓의 아이디를 알아야함

    //취소
    //결제도 취소해야함... 결제 취소는 어떻게?
    @Secured("ROLE_ADMIN")
    @GetMapping("/rejectOrder/{bucket_id}")
    public ApplicationResponse<?> rejectOrder(@AuthenticationPrincipal PrincipalDetails principalDetails,@PathVariable("bucket_id") Long bucketId){
        Long id = principalDetails.getMember().getId();
        orderService.rejectOrder(id, bucketId);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK);
    }

    //주문 확인
    @Secured("ROLE_ADMIN")
    @PostMapping("/acceptOrder/{bucket_id}")
    public ApplicationResponse<?> acceptOrder(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable("bucket_id") Long bucketId){
        Long id = principalDetails.getMember().getId();
        orderService.acceptOrder(id,bucketId);
        return ApplicationResponse.ok(ErrorCode.SUCCESS_OK);
    }
}
