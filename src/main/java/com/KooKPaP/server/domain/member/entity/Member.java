package com.KooKPaP.server.domain.member.entity;

import com.KooKPaP.server.global.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "Member")
@SQLDelete(sql = "UPDATE member SET deleted_at = NOW() where id = ?") // SQL Delete 쿼리 시, 논리적 삭제로 바인딩되도록 하기 위한 용도
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 무분별한 생성자 사용을 막는 용도
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "bigint")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password") // 카카오 로그인 시, 비밀번호 불필요하므로 nullable = true로 설정
    private String password;

    @Column(name = "type")
    @Enumerated(EnumType.STRING) // ENUM Type을 DB에서는 String으로 관리하도록 설정
    private LoginType type;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "address", columnDefinition = "text") // MANAGER 에게는 딱히 필요 없을것 같아서 nullable = true로 설정
    private String address;
}
