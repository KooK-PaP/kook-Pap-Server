package com.KooKPaP.server.domain.order.OrderService;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.order.dto.request.BucketReq;
import com.KooKPaP.server.domain.order.dto.response.BucketRes;
import com.KooKPaP.server.domain.order.dto.response.PurchaseRes;
import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.BucketState;
import com.KooKPaP.server.domain.order.entity.Purchase;
import com.KooKPaP.server.domain.order.repository.BucketRepository;
import com.KooKPaP.server.domain.order.repository.PurchaseRepository;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final BucketRepository bucketRepository;
    private final PurchaseRepository purchaseRepository;

    @Transactional
    public BucketRes order(Long id, BucketReq bucketReq){
        //valid : 사용자 정보가 Customer인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.CUSTOMER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        boolean purchaseSuccessful = purchase(id, bucketReq);

        //Business Logic : 주문 하기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 저장하고 !!결제하기!!

        if (purchaseSuccessful) {
        Bucket bucket = bucketReq.from(member);
        bucketRepository.save(bucket);


        //Response
        BucketRes bucketRes = BucketRes.of(bucket);
        return bucketRes;

        } else {
            throw new CustomException(ErrorCode.PURCHASE_FAILED);
        }
    }

    @Transactional
    public boolean purchase(Long id, BucketReq bucketReq){
        //valid : 사용자 정보가 Customer인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.CUSTOMER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        //Business Logic : 주문 하기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 저장


        //Response

        return true;
    }

    @Transactional
    public BucketRes cancelOrder(Long id, Long bucketId){
        //valid : 사용자 정보가 Customer인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.CUSTOMER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        //Business Logic : 주문 하기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 저장
        Bucket bucket = bucketRepository.findBucketByBucketId(bucketId).orElseThrow(() -> new CustomException(ErrorCode.BUCKET_NOT_FOUND));

        if (bucket.getState() == BucketState.AWAITING_ACCEPT){
            bucket.updateState(BucketState.REFUSED);
            bucketRepository.save(bucket);
        } else {
            throw new CustomException(ErrorCode.INVALID_BUCKET_STATE);
        }

        //Response
        BucketRes bucketRes = BucketRes.of(bucket);
        return bucketRes;
    }

    @Transactional
    public BucketRes myOrdersDetail(Long id, Long bucketId){
        //valid : 사용자 정보가 Customer인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.CUSTOMER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        //Business Logic : 주문 하기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 보여주기
        Bucket bucket = bucketRepository.findBucketByBucketId(bucketId).orElseThrow(() -> new CustomException(ErrorCode.BUCKET_NOT_FOUND));



        //Response
        BucketRes bucketRes = BucketRes.of(bucket);
        return bucketRes;
    }

    @Transactional
    public List<BucketRes> myOrders(Long id, BucketReq bucketReq){
        //valid : 사용자 정보가 Customer인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.CUSTOMER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        //Business Logic : 주문 보기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 보기 리스트에 담아서!
        List<Bucket> bucketList = bucketRepository.findBucketsById(id);
        List<BucketRes> bucketRes = bucketList.stream()
                .map(BucketRes::of)
                .collect(Collectors.toList());

        //Response

        return bucketRes;
    }




    @Transactional
    public void rejectOrder(Long id, Long bucketId){
        //valid : 사용자 정보가 manager인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        //Business Logic : 주문 취소하기 -- 버킷아이디를 받고, 해당 버킷의 state를 REFUSED로 설정해놓기
        Bucket bucket = bucketRepository.findBucketByBucketId(bucketId).orElseThrow(() -> new CustomException(ErrorCode.BUCKET_NOT_FOUND));
        if (bucket.getState() == BucketState.AWAITING_ACCEPT){
            bucket.updateState(BucketState.REFUSED);
            bucketRepository.save(bucket);
        } else {
            throw new CustomException(ErrorCode.INVALID_BUCKET_STATE);
        }

        //Response

    }


    @Transactional
    public void acceptOrder(Long id, Long bucketId){
        //valid : 사용자 정보가 Manager인지 확인하기
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)){
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }
        Bucket bucket = bucketRepository.findBucketByBucketId(bucketId).orElseThrow(() -> new CustomException(ErrorCode.BUCKET_NOT_FOUND));

        //Business Logic : 주문 확정하기 -- 버킷아이디를 받고, 해당 버킷의 state를 ACCEPTED로 설정해놓기
        bucket.updateState(BucketState.ACCEPTED);


        //Response
    }
}