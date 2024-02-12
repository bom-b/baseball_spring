package com.baseball.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface AuthDao {

    // OauthId를 통해 아이디 가져오기
    Integer getIdByOauthId(@Param("with_oauth") String withOauth, @Param("oauth_id") String oauthId);

    // 회원가입시 닉네임 중복체크
    Integer checkNicknameDuplication(@Param("nickname") String nickname);

    // OauthId를 통해 회원가입
    void singUpWithOauth(@Param("with_oauth") String withOauth, @Param("oauth_id") String oauthId, @Param("nickname") String nickname);

    // 프로필 이미지 이름 업데이트
    void updateProfileImg(@Param("id") Integer id, @Param("profile_img") String fileName);

    // id를 통해 회원의 닉네임 가져오기
    String getNickname(@Param("id") Integer id);

    // id를 통해 회원의 프로필사진 이름 가져오기
    String getProfileImage(@Param("id") Integer id);
}