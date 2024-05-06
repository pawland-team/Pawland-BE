package com.pawland.auth.dto.response;

import com.pawland.user.domain.LoginType;
import com.pawland.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class OAuthAttributes {

    private static final String TEMP_NICKNAME = "임시 닉네임";
    private String nickname;
    private String email;
    private String profileImage;
    private String provider;

    @Builder
    public OAuthAttributes(String nickname, String email, String profileImage, String provider) {
        this.nickname = nickname;
        this.email = email;
        this.profileImage = profileImage;
        this.provider = provider;
    }

    public static OAuthAttributes of(String providerName, Map<String, Object> attributes) throws IllegalArgumentException {
        switch (providerName) {
            case "카카오":
                return ofKakao(providerName, attributes);
            case "구글":
                return ofGoogle(providerName, attributes);
            case "네이버":
                return ofNaver(providerName, attributes);
            default:
                throw new IllegalArgumentException("허용되지 않은 접근입니다.");
        }
    }

    public User toUser() {
        return User.builder()
            .nickname(nickname)
            .email(email)
            .password("oauth2")
            .profileImage(profileImage)
            .type(LoginType.fromString(provider))
            .build();
    }

    private static OAuthAttributes ofKakao(String providerName, Map<String, Object> attributes) {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");
        return OAuthAttributes.builder()
            .nickname(TEMP_NICKNAME)
            .email(account.get("email") + "/" + providerName)
            .profileImage((String) profile.get("profile_image_url"))
            .provider(providerName)
            .build();
    }

    private static OAuthAttributes ofGoogle(String providerName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
            .nickname(TEMP_NICKNAME)
            .email(attributes.get("email") + "/" + providerName)
            .profileImage((String) attributes.get("picture"))
            .provider(providerName)
            .build();
    }

    private static OAuthAttributes ofNaver(String providerName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
            .nickname(TEMP_NICKNAME)
            .email(response.get("email") + "/" + providerName)
            .profileImage((String) response.get("profile_image"))
            .provider(providerName)
            .build();
    }
}