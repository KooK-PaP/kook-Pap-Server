package com.KooKPaP.server.domain.restaurant.service;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.entity.Operation;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import com.KooKPaP.server.domain.restaurant.repository.OperationRepository;
import com.KooKPaP.server.domain.restaurant.repository.RestaurantRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RestaurantService {

    private final MemberRepository memberRepository;
    private final RestaurantRepository restaurantRepository;
    private final OperationRepository operationRepository;

    @Transactional
    public RestaurantRes register(Long id, RestaurantReq restaurantReq) {
        // Validation: 사용자 정보가 MANAGER인지 확인
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)) {
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        // Business Logic: 레스토랑/운영시간 저장
        Operation operation = restaurantReq.getOperation().from();
        operationRepository.save(operation);
        Restaurant restaurant = restaurantReq.from(member, operation);
        restaurantRepository.save(restaurant);

        // Response
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant);
        return restaurantRes;
    }

    @Transactional
    public RestaurantRes update(Long id, Long restaurantId, RestaurantReq restaurantReq) {
        // Validation: 사용자 정보가 MANAGER인지 확인
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)) {
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }
        Restaurant restaurant = restaurantRepository.findRestaurantByIdAndDeletedAtIsNull(restaurantId).orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // Business Logic
        restaurant.update(restaurantReq);
        restaurant.getOperation().update(restaurantReq.getOperation()); // RequestDto에서 NotNull 어노테이션으로 유효성 검증 진행 (NPE 발생 가능성 예방)

        // Response
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant);
        return restaurantRes;
    }

    @Transactional
    public Void delete(Long id, Long restaurantId) {
        // Validation
        if(restaurantRepository.existsRestaurantByIdAndMemberIdAndDeletedAtIsNull(restaurantId, id).equals(Boolean.FALSE)) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        // Business Logic
        restaurantRepository.deleteById(restaurantId);
        //operationRepository.deleteByRestaurantId(restaurantId);

        // Response
        return null;
    }

    public RestaurantRes getOne(Long restaurantId) {
        // Validation
        Restaurant restaurant = restaurantRepository.findRestaurantByIdAndDeletedAtIsNull(restaurantId).orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // Business Logic
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant);

        // Response
        return restaurantRes;
    }

    public List<RestaurantRes> getMy(Long id) {
        // Validation: Member에 대한 권한 검증 필요 여부 판단

        // Business Logic
        List<RestaurantRes> restaurantRes = new ArrayList<>();
        List<Object[]> restaurantList = restaurantRepository.findRestaurantWithOperationByMemberId(id).orElse(null);

        // Response
        return restaurantRes;
    }

    public Slice<RestaurantRes> getAll(Long cursor, Pageable pageable) {
        // Validation

        // Business Logic

        // Response
        return null;
    }
}
