package com.mysite.spring.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Map<String, Object> attributes = oauth2User.getAttributes();

        // 카카오 사용자 정보 추출
        String id = attributes.get("id") != null ? attributes.get("id").toString() : null;
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = kakaoAccount.get("email").toString();

        // 사용자 저장 또는 업데이트
        SiteUser user = saveOrUpdateUser(id, email);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userNameAttributeName
        );
    }

    private SiteUser saveOrUpdateUser(String id, String email) {
        SiteUser user = userRepository.findByEmail(email)
                .orElse(new SiteUser()); // SiteUser 객체를 새로 생성합니다.

        user.setKakaoId(id);  // kakaoId를 설정합니다.
        user.setEmail(email);  // 이메일을 설정합니다.
        user.setRole(UserRole.USER);  // 기본 역할 설정 (USER)

        return userRepository.save(user);  // 사용자 저장 또는 업데이트
    }

}
