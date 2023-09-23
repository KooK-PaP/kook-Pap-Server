package com.KooKPaP.server.domain.restaurant.service;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.entity.Operation;
import com.KooKPaP.server.domain.restaurant.entity.Restaurant;
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

    @Transactional
    public RestaurantRes register(Long id, RestaurantReq restaurantReq) {
        // Validation: 사용자 정보가 MANAGER인지 확인
        Member member = memberRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.AUTH_MEMBER_NOT_FOUND));
        if(!member.getRole().equals(Role.MANAGER)) {
            throw new CustomException(ErrorCode.AUTH_NOT_ALLOWED_ACCESS);
        }

        // Business Logic
        Restaurant restaurant = restaurantReq.toEntity(member);
        restaurantRepository.save(restaurant);

        // Response
        RestaurantRes restaurantRes = new RestaurantRes();
        return restaurantRes;
    }
}
