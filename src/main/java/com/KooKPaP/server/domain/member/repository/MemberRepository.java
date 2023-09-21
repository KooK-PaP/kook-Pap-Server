package com.KooKPaP.server.domain.member.repository;

import com.KooKPaP.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // Id로 Member 조회하기 위함. (대부분 이것으로 member 찾음)
    Optional<Member> findById(Long id);
    // Email 로 member 조회하기 위함. (Kakao 서버에서 제공하는 정보(email)로 member 찾음 )
    Optional<Member> findByEmail(String email);
    // email로 이미 가입한 회원이 있는지 조회. 중복: true, 아니면 false
    boolean existsByEmail(String email);
    // DB에서 member를 삭제하기 위함.(deletetAt 이 현재시간으로 설정됨.)
    void delete(Member member);
}
