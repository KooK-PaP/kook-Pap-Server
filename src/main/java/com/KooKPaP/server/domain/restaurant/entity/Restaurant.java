package com.KooKPaP.server.domain.restaurant.entity;

import com.KooKPaP.server.domain.member.entity.Member;
import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Member")
@SQLDelete(sql = "UPDATE restaurant SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Restaurant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "time", columnDefinition = "text")
    private String time;

    @Column(name = "call_number", nullable = false)
    private String callNumber;

    @Column(name = "introduction", nullable = false, columnDefinition = "text")
    private String introduction;
}
