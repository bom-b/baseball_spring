package com.baseball.controller;


import com.baseball.service.RoomService;
import com.baseball.utils.JwtUtil;
import com.baseball.vo.RoomVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RoomService roomService;

    // 대기방 만들기
    @PostMapping("/makeRoom")
    public ResponseEntity<?> makeRoom(@RequestBody Map<String, String> param) {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);

        RoomVO roomVO = new RoomVO();
        roomVO.setRoom_name(param.get("name"));
        roomVO.setTime(Integer.parseInt(param.get("time")));

        Integer roomId = roomService.makeRoom(id, roomVO);

        // 새로 만든 방의 id 리턴
        return ResponseEntity.ok(roomId);
    }

    // 대기방 리스트 가져오기
    @GetMapping("/getRoomList")
    public ResponseEntity<?> getRoomList() {
        List<RoomVO> roomList = roomService.getRoomList();
        return ResponseEntity.ok(roomList);
    }
    
    // 유저의 방 정보 가져오기
    @GetMapping("/getUsersRoomInfo")
    public ResponseEntity<?> getUsersRoomInfo() {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);

        Map<String, String> roomInfo = new HashMap<>();

        RoomVO roomVO = roomService.getRoomByUserId(id);
        if(roomVO != null) {
            roomInfo.put("room_id", String.valueOf(roomVO.getRoom_id()));
            roomInfo.put("status", String.valueOf(roomVO.getStatus()));
            roomInfo.put("room_name", roomVO.getRoom_name());
            roomInfo.put("player1", String.valueOf(roomVO.getPlayer1()));
            roomInfo.put("player2", String.valueOf(roomVO.getPlayer2()));
            roomInfo.put("head", String.valueOf(roomVO.getHead()));
        }

        return ResponseEntity.ok(roomInfo);
    }

    @PostMapping("/destroyRoom")
    public ResponseEntity<?> destroyRoom(@RequestBody String roomId) {
        roomService.destroyRoom(roomId);
        return ResponseEntity.ok("success");
    }

}
