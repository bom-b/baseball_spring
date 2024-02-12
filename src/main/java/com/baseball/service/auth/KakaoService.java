package com.baseball.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class KakaoService {


    // 프론트에서 받은 코드를 통해 카카오 서버로 토큰을 요청
    public ResponseEntity<String> getKakaoToken(String code) {
        // POST 방식으로 key=value 데이터를 요청 (카카오쪽으로)
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "5503b5836935e429191e7c53ce32baa2");
        params.add("redirect_uri", "http://localhost:3000/singUp/kakaoJoin");
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        // Http 요청하기 - POST 방식으로 - 그리고 response 변수의 응답 받음

        return rt.exchange(
            "https://kauth.kakao.com/oauth/token",
            HttpMethod.POST,
            kakaoTokenRequest,
            String.class
        );
    }

    // response를 통해 access_token 가져오기
    public String getAccessoken(ResponseEntity<String> response) {
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 문자열을 JsonNode로 읽기
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // access_token 키의 값 추출
            return jsonNode.get("access_token").asText();

        } catch (Exception e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();
        }

        return null;
    }


    // 디코딩된 토큰 가져오기
    public String getDecodedIDToken(ResponseEntity<String> response) {
        String responseBody = response.getBody();

        // Jackson ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // JSON 문자열을 JsonNode로 읽기
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // id_token 키의 값 추출
            String idToken = jsonNode.get("id_token").asText();

            // id_token을 .을 기준으로 분리
            String[] jwtParts = idToken.split("\\.");

            // id_token중에서도 유저 정보(페이로드)가 들어있는 [1]번째 배열을 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(jwtParts[1]);
            return new String(decodedBytes, StandardCharsets.UTF_8);

        } catch (Exception e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();
        }

        return null;
    }

    // 디코딩된 토큰을 통해 원하는 필드 받기
    private String getValueFromDecodedToken(String decodedIDToken, String fieldName) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 JsonNode로 읽기
            JsonNode jsonNode = objectMapper.readTree(decodedIDToken);

            // 지정된 필드의 값을 추출
            return jsonNode.get(fieldName).asText();
        } catch (Exception e) {
            // JSON 파싱 오류 처리
            e.printStackTrace();
        }
        return null;
    }

    // 디코딩된 토큰을 넣어서 사용자 식별자 가져오기
    public String getSub(String decodedIDToken) {
        return getValueFromDecodedToken(decodedIDToken, "sub");
    }

    // 디코딩된 토큰을 넣어서 이메일 가져오기
    public String getEmail(String decodedIDToken) {
        return getValueFromDecodedToken(decodedIDToken, "email");
    }

    // 디코딩된 토큰을 넣어서 닉네임 가져오기
    public String getNickname(String decodedIDToken) {
        return getValueFromDecodedToken(decodedIDToken, "nickname");
    }

    // 디코딩된 토큰을 넣어서 프로필 사진 가져오기
    public String getProfileImage(String decodedIDToken) {
        return getValueFromDecodedToken(decodedIDToken, "picture");
    }

}
