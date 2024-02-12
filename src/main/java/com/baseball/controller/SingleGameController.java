package com.baseball.controller;

import com.baseball.service.SingleGameService;
import com.baseball.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/singleGame")
@Slf4j
public class SingleGameController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SingleGameService singleGameService;

    @PostMapping("/record")
    public ResponseEntity<?> updateRecord(@RequestBody Integer turn) {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);

        // 최고기록 업데이트
        singleGameService.updateRecord4(id, turn);

        return ResponseEntity.ok().body("반영성공");
    }
}
