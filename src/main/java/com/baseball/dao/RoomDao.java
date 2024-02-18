package com.baseball.dao;

import com.baseball.vo.RoomVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper
public interface RoomDao {

    // 중복되지 않는 새로운 룸 아이디 생성
    Integer generateRoomId();

    // 모두에게 공개되는 룸 생성
    void createRoom(@Param("userId") Integer userId, @Param("newRoomId") Integer newId, @Param("room") RoomVO roomVO);

    // 룸 리스트 가져오기
    List<RoomVO> getRoomList();

    // 유저 아이디가 속한 룸 정보 추출
    RoomVO getRoomByUserId(@Param("userId") Integer id);

    // 룸 파괴
    void destroyRoom(@Param("roomId") String roomId);
}
