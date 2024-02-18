package com.baseball.service;

import com.baseball.dao.RoomDao;
import com.baseball.vo.RoomVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomService {

    @Autowired
    private RoomDao roomDao;

    public Integer makeRoom(Integer userId, RoomVO roomVO) {
        Integer newId = roomDao.generateRoomId();
        roomDao.createRoom(userId, newId, roomVO);
        return newId;
    }

    public List<RoomVO> getRoomList() {
        return roomDao.getRoomList();
    }

    public RoomVO getRoomByUserId(Integer id) {
         return roomDao.getRoomByUserId(id);
    }

    public void destroyRoom(String roomId) {
        roomDao.destroyRoom(roomId);
    }
}
