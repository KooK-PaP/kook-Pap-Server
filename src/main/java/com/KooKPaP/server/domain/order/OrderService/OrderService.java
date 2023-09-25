package com.KooKPaP.server.domain.order.OrderService;

import com.KooKPaP.server.domain.member.repository.MemberRepository;
import com.KooKPaP.server.domain.order.dto.request.OrderReq;
import com.KooKPaP.server.domain.order.dto.response.OrderRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;

    @Transactional
    public OrderRes order(Long id, OrderReq orderReq){


        return orderRes;
    }
}
