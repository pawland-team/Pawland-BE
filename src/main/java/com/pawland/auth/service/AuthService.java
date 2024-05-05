package com.pawland.auth.service;

import com.pawland.auth.dto.response.OAuthAttributes;
import com.pawland.auth.dto.response.OauthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public OAuthAttributes getUserInfoByOauth2(String code, String provider) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId(provider);
        OauthTokenResponse tokenResponse = requestAccessToken(code, registration);
        return getUerProfile(tokenResponse.getAccessToken(), registration);
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

    private OAuthAttributes getUerProfile(String accessToken, ClientRegistration registration) {
        Map<String, Object> userAttributes = requestUserAttributes(registration, accessToken);
        String clientName = registration.getClientName();
        return OAuthAttributes.of(clientName, userAttributes);
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
}
