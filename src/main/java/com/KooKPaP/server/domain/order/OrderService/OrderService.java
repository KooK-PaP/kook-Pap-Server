package com.KooKPaP.server.domain.order.OrderService;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.order.dto.request.BucketReq;
import com.KooKPaP.server.domain.order.dto.response.BucketRes;
import com.KooKPaP.server.domain.order.dto.response.PurchaseRes;
import com.KooKPaP.server.domain.order.entity.Bucket;
import com.KooKPaP.server.domain.order.entity.Purchase;
import com.KooKPaP.server.domain.order.repository.BucketRepository;
import com.KooKPaP.server.domain.order.repository.PurchaseRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

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

        //Business Logic : 주문 하기 (언제, 어느 가게, 어느 메뉴, 메뉴의 수량) 주문 내역 저장
        Bucket bucket = bucketReq.from(member);
        Purchase purchase = bucketReq.getPurchaseReq().from(bucket);
        bucketRepository.save(bucket);
        purchaseRepository.save(purchase);

        //Response
        PurchaseRes purchaseRes = PurchaseRes.of(purchase);
        BucketRes bucketRes = BucketRes.of(bucket, purchaseRes);
        return bucketRes;
    }
}
