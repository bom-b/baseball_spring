package com.baseball.controller.auth;

import com.baseball.dao.AuthDao;
import com.baseball.service.S3UploadService;
import com.baseball.service.auth.AuthService;
import com.baseball.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthService authService;

    @Autowired
    private S3UploadService s3UploadService;

    // 닉네임 중복체크 0: 중복아님 1: 중복
    @GetMapping("checkNicknameDuplication/{nickname}")
    public ResponseEntity<Integer> checkNicknameDuplication(@PathVariable("nickname") String nickname) {
        Integer result = authService.checkNicknameDuplication(nickname);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Oauth를 통한 회원가입 시키기
    @PostMapping("singUpWithOauth")
    public ResponseEntity<String> singUpWithOauth(@RequestParam("nickname") String nickname,
                                                  @RequestParam("withOauth") String withOauth,
                                                  @RequestParam("oauthId") String oauthId,
                                                  @RequestParam(value = "image", required = false) MultipartFile image) {
        // 파라미터로 이미지가 넘어왔다면 true
        boolean isInImage = image != null;

        // step1. db에 회원 정보 저장
        Integer id = authService.singUpWithOauth(withOauth, oauthId, nickname, isInImage);

        // step2. 저장하면서 받은 id값을 파일의 이름으로 만들어 s3에 저장
        if (isInImage) {
            String s3FileName = id.toString();
            String path = s3UploadService.saveWithMultipartFile("user/profileImg/", s3FileName, image);
            if (path == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S3 업로드 중 오류가 발생했습니다.");
            }
        }

        return ResponseEntity.ok("회원가입 정상처리");
    }

    // 토큰체크
    @GetMapping("/checkToken")
    public ResponseEntity<?> checkToken() {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);

        // 사용자 정보 보내기
        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", Objects.requireNonNull(id).toString());
        responseData.put("nickname", authService.getNickname(id));
        responseData.put("profileImg", authService.getProfileImage(id));

        return ResponseEntity.ok().body(responseData);
    }

}
