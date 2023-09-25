package com.KooKPaP.server.domain.restaurant.service;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.OperationRes;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.entity.Operation;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
import com.KooKPaP.server.domain.restaurant.repository.OperationRepository;
import com.KooKPaP.server.domain.restaurant.repository.RestaurantRepository;
import com.KooKPaP.server.global.common.exception.CustomException;
import com.KooKPaP.server.global.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Restaurant restaurant = restaurantReq.from(member);
        Operation operation = restaurantReq.getOperation().from(restaurant);
        restaurantRepository.save(restaurant);
        operationRepository.save(operation);


        // Response
        OperationRes operationRes = OperationRes.of(operation);
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant, operationRes);
        return restaurantRes;
    }

    @Transactional
    public RestaurantRes update(Long id, Long restaurantId, RestaurantReq restaurantReq) {
        // Validation: 사용자 정보가 MANAGER인지 확인
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)) {
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        // Business Logic
        Restaurant restaurant = restaurantRepository.findRestaurantByIdAndDeletedAtIsNull(restaurantId).orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));
        Operation operation = operationRepository.findOperationByRestaurantIdAndDeletedAtIsNull(restaurantId).orElse(null);
        restaurant.update(restaurantReq);
        operation.update(restaurantReq.getOperation());

        // Response
        OperationRes operationRes = OperationRes.of(operation);
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant, operationRes);
        return restaurantRes;
    }

    @Transactional
    public Void delete(Long id, Long restaurantId) {
        // Validation
        if(restaurantRepository.existsRestaurantByIdAndMemberIdAAndDeletedAtIsNull(restaurantId, id).equals(Boolean.FALSE)) {
            throw new CustomException(ErrorCode.RESTAURANT_NOT_FOUND);
        }

        // Business Logic
        restaurantRepository.deleteById(restaurantId);

        // Response
        return null;
    }

    public RestaurantRes getOne(Long restaurantId) {
        // Validation
        Restaurant restaurant = restaurantRepository.findRestaurantByIdAndDeletedAtIsNull(restaurantId).orElseThrow(() -> new CustomException(ErrorCode.RESTAURANT_NOT_FOUND));

        // Business Logic
        Operation operation = operationRepository.findOperationByRestaurantIdAndDeletedAtIsNull(restaurantId).orElse(null);
        OperationRes operationRes = OperationRes.of(operation);
        RestaurantRes restaurantRes = RestaurantRes.of(restaurant, operationRes);

        // Response
        return restaurantRes;
    }
}
