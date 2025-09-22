package com.iara.core.proxy;

import com.iara.core.exception.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.Objects;


@Service
public class GoogleProxy {

    @Value("${google.soo.token:https://oauth2.googleapis.com/token}")
    private String googleTokenUrl;

    public String exchangeCodeToJwt(String clientId, String clientSecret, String code, String redirectUri) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        httpHeaders.set("code", code);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, httpHeaders);

        ResponseEntity<LinkedHashMap> response = restTemplate.exchange(googleTokenUrl, HttpMethod.POST, entity, LinkedHashMap.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            LinkedHashMap<String, Object> responseBody = response.getBody();
            if (Objects.isNull(responseBody) || Objects.isNull(responseBody.get("id_token"))) {
                throw new InvalidCredentialsException("There was an error while integrating with Google.");
            }

            return responseBody.get("id_token").toString();
        }
        throw new InvalidCredentialsException("There was an error while integrating with Google.");
    }
}
