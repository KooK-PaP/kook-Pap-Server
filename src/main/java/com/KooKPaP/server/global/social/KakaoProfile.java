package com.KooKPaP.server.global.social;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoProfile {

    private Long id;     // 회원 번호
    private String connected_at;     // 서비스에 연결 완료된 시각
    private KakaoAccount kakao_account;  // 카카오 계정 정보


    @Getter
    @ToString
    public class KakaoAccount {     // 주석으로 ???된 부분들은 Deprecated 됨
        private Profile profile; // 프로필 정보
        private String email;    // 카카오계정 대표 이메일


        @Getter
        @ToString
        public class Profile {
            private String nickname; // 닉네임
        }
    }
}
