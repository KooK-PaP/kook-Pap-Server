package com.KooKPaP.server.domain.restaurant.service;

import com.KooKPaP.server.domain.member.entity.LoginType;
import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.domain.member.entity.Role;
import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.restaurant.dto.request.OperationReq;
import com.KooKPaP.server.domain.restaurant.dto.request.RestaurantReq;
import com.KooKPaP.server.domain.restaurant.dto.response.RestaurantRes;
import com.KooKPaP.server.domain.restaurant.repository.RestaurantRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Autowired
    private final PasswordEncoder passwordEncoder;
    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MemberRepository memberRepository;

    public RestaurantServiceTest(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Test
    @DisplayName("가게/운영시간 정보를 받아 가게/운영시간 정보를 생성한다.")
    public void registerSuccessCase() {
        // given
        Member member = createMember();
        OperationReq operationReq = createOperationReqDto();
        RestaurantReq restaurantReq = createRestaurantReqDto(operationReq);

        // when
        RestaurantRes restaurantRes = restaurantService.register(member.getId(), restaurantReq);

        // then
        Assertions.assertThat(restaurantRes).isNotNull();
        Assertions.assertThat(restaurantRes.getMemberId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("")
    public void registerFailCase() {
        // given
        RestaurantReq restaurantReq = createRestaurantReqDto(null);

        // when

        // then
    }

    private Member createMember() {
        return Member.builder()
                .name("테스트 이름")
                .email("test@test.com")
                .password(passwordEncoder.encode("test1234"))
                .address("")
                .type(LoginType.GENERAL)
                .role(Role.MANAGER)
                .build();
    }

    private RestaurantReq createRestaurantReqDto(OperationReq operationReq) {
        return RestaurantReq.builder()
                .name("테스테 가게")
                .introduction("테스트 정보입니다.")
                .callNumber("01012345678")
                .address("대한민국 어딘가")
                .operation(operationReq)
                .build();
    }

    private OperationReq createOperationReqDto() {
        return OperationReq.builder()
                .monOpen(LocalTime.now())
                .monClose(LocalTime.now())
                .tueOpen(LocalTime.now())
                .tueClose(LocalTime.now())
                .wedOpen(LocalTime.now())
                .wedClose(LocalTime.now())
                .thuOpen(LocalTime.now())
                .thuClose(LocalTime.now())
                .friOpen(LocalTime.now())
                .friClose(LocalTime.now())
                .satOpen(LocalTime.now())
                .satClose(LocalTime.now())
                .sunOpen(LocalTime.now())
                .sunClose(LocalTime.now())
                .build();
    }
}