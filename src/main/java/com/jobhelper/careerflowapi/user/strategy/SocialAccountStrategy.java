package com.jobhelper.careerflowapi.user.strategy;

import com.jobhelper.careerflowapi.user.application.LoginResult;
import com.jobhelper.careerflowapi.user.domain.enums.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SocialAccountStrategy implements AccountStrategy {

    public LoginResult authenticate(Provider provider, String code) {
        // TODO: OAuth2 플로우 구현 예정
        // 1. provider에 code를 전송해 access token 발급
        // 2. access token으로 provider 사용자 정보(email, providerId) 조회
        // 3. providerId로 기존 UserAccount 조회
        // 4. 없으면 자동 회원가입(User + UserAccount 생성) 후 LoginEvent 발행
        // 5. 있으면 LoginEvent 발행 → AuthEventHandler에서 JWT 발급
        throw new UnsupportedOperationException("소셜 로그인은 아직 구현되지 않았습니다.");
    }
}
