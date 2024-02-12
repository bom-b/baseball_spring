package com.baseball.dao;

import com.baseball.vo.MembersRecordVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MemberDao {

    // 회원의 게임 기록 가져오기
    MembersRecordVO getMembersRecord(@Param("id") Integer id);

    // 유저 정보 수정
    void updateProfile(@Param("id") Integer id, @Param("nickname") String nickname);

    // 유저 삭제
    void withdrawal(@Param("id") Integer id);
}
