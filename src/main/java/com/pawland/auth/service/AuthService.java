package com.pawland.auth.service;

import com.pawland.auth.dto.response.OAuthAttributes;
import com.pawland.auth.dto.response.OauthTokenResponse;
import com.pawland.user.domain.User;
import com.pawland.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private static final String TEMP_NICKNAME_PREFIX = "임시닉네임";
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final UserRepository userRepository;

    @Transactional
    public User oauth2Login(String code, String provider) {
        validateProvider(provider);
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);
        OauthTokenResponse tokenResponse = requestAccessToken(code, registration);
        User uerProfile = getUerProfile(tokenResponse.getAccessToken(), registration);
        return updateOrSave(uerProfile);
    }

    private void validateProvider(String provider) {
        List<String> validProvider = List.of("kakao", "naver", "google");
        if (!validProvider.contains(provider)) {
            throw new IllegalArgumentException("허용되지 않은 접근입니다.");
        }
    }

    private OauthTokenResponse requestAccessToken(String code, ClientRegistration registration) {
        return WebClient.create()
            .post()
            .uri(registration.getProviderDetails().getTokenUri())
            .headers(header -> {
                header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                header.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
            })
            .bodyValue(createTokenRequest(code, registration))
            .retrieve()
            .bodyToMono(OauthTokenResponse.class)
            .block();
    }

    private MultiValueMap<String, String> createTokenRequest(String code, ClientRegistration registration) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", registration.getRedirectUri());
        formData.add("client_secret", registration.getClientSecret());
        formData.add("client_id", registration.getClientId());
        return formData;
    }

    private User getUerProfile(String accessToken, ClientRegistration registration) {
        Map<String, Object> userAttributes = requestUserAttributes(registration, accessToken);
        userAttributes.put("temp_nickname", createNonDuplicateNickname());
        return OAuthAttributes.of(registration.getClientName(), userAttributes).toUser();
    }

    private Map<String, Object> requestUserAttributes(ClientRegistration registration, String accessToken) {
        return WebClient.create()
            .get()
            .uri(registration.getProviderDetails().getUserInfoEndpoint().getUri())
            .headers(header -> header.setBearerAuth(accessToken))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();
    }

    private String createNonDuplicateNickname() {
        String tempNickname = createRandomNickname();
        while (isDuplicateNickname(tempNickname)) {
            tempNickname = createRandomNickname();
        }
        return tempNickname;
    }

    private String createRandomNickname() {
        Random random = new Random();
        int randomNumber = random.nextInt(999999) + 1;
        return TEMP_NICKNAME_PREFIX + String.format("%06d", randomNumber);
    }

    private boolean isDuplicateNickname(String nickname) {
        return userRepository.findByNickname(nickname).isPresent();
    }

    private User updateOrSave(User uerProfile) {
        User oauth2User = userRepository.findByEmail(uerProfile.getEmail())
            .map(user -> user.updateOauth2Profile(uerProfile))
            .orElse(uerProfile);
        return userRepository.save(oauth2User);
    }
}
