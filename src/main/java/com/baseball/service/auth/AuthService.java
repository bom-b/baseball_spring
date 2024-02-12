package com.baseball.service.auth;

import com.baseball.dao.AuthDao;
import com.baseball.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthDao authDao;

    @Value("${jwt.secret}")
    private String secretKey;

    private Long expiredMs = 1000 * 60 * 60L; // 1시간

    // OauthId를 통해 가입된 회원인지 확인하기
    public Integer checkIdByOauthId(String withOauth, String oauthId) {
        return authDao.getIdByOauthId(withOauth, oauthId);
    }

    // id를 통해 회원의 닉네임 가져오기
    public String getNickname(Integer id) {
        return authDao.getNickname(id);
    }

    // id를 통해 회원의 프로필사진 이름 가져오기
    public String getProfileImage(Integer id) {
        return authDao.getProfileImage(id);
    }

    // Oauth 로그인
    // OauthId를 통해 토큰 발급시키기
    public String loginWithOauthId(String withOauth, String oauthId) {
        Integer userName = authDao.getIdByOauthId(withOauth, oauthId);
        return JwtUtil.createJwt(userName, expiredMs);
    }

    // 회원가입시 닉네임 중복체크
    public Integer checkNicknameDuplication(String nickname) {
        return authDao.checkNicknameDuplication(nickname);
    }

    // Oauth 회원가입
    // 들어온 사진이 있을 경우 프로필 이미지 이름 바꿔주기
    public Integer singUpWithOauth(String withOauth, String oauthId, String nickname, boolean isInImage) {
        authDao.singUpWithOauth(withOauth, oauthId, nickname);
        Integer id = authDao.getIdByOauthId(withOauth, oauthId);
        if (isInImage) {
            authDao.updateProfileImg(id, id.toString());
        } else {
            authDao.updateProfileImg(id, "default");
        }
        return id;
    }


}
