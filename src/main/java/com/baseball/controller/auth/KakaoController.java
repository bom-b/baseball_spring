package com.baseball.controller.auth;

import com.baseball.service.auth.AuthService;
import com.baseball.service.auth.KakaoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/kakao")
@Slf4j
public class KakaoController {

    @Autowired
    private AuthService authService;

    @Autowired
    private KakaoService kakaoService;

    @GetMapping("kakaoLogin/{code}")
    public ResponseEntity<Map<String, String>> kakaoLogin(@PathVariable("code") String code) {
        // 프론트에서 받은 코드를 통해 카카오 서버로 토큰을 요청
        ResponseEntity<String> tokenResponse = kakaoService.getKakaoToken(code);

        // 토큰을 통해 디코딩된 유저 정보 리스트 반환받기
        String decodedIDToken = kakaoService.getDecodedIDToken(tokenResponse);

        // 토큰을 통해 access_token 가져오기
        String accessToken = kakaoService.getAccessoken(tokenResponse);

        // 유저의 정보를 통해 식별자를 가져옴
        String kakaoSub = kakaoService.getSub(decodedIDToken);

        // 식별자를 통해 회원가입 여부를 확인
        Integer id = authService.checkIdByOauthId("kakao",kakaoSub);

        Map<String, String> responseData = new HashMap<>();
        if (id == null) { // 비회원이면 회원가입할 때 채워줄 정보 보내기
            responseData.put("isSign", "false");
            responseData.put("ki", kakaoSub); //회원가입할 때 oauth_id컬럼을 채워줄 친구

        } else { // 회원이면 토큰 발급해서 보내기
            String token = authService.loginWithOauthId("kakao", kakaoSub);
            responseData.put("isSign", "true");
            responseData.put("token", token);
            responseData.put("id", id.toString());
            responseData.put("nickname", authService.getNickname(id));
            responseData.put("accessToken", accessToken);
        }

        return ResponseEntity.ok(responseData);

    }

}
