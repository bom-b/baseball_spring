package com.baseball.controller;

import com.baseball.service.MemberService;
import com.baseball.service.S3UploadService;
import com.baseball.service.auth.AuthService;
import com.baseball.utils.JwtUtil;
import com.baseball.vo.MembersRecordVO;
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
@RequestMapping("/member")
@Slf4j
public class MemberController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private S3UploadService s3UploadService;

    @GetMapping("/getProfileInfo")
    public ResponseEntity<?> getProfileInfo() {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);

        // 사용자 정보 보내기
        Map<String, String> responseData = new HashMap<>();
        Integer id = JwtUtil.getID(token);
        responseData.put("id", Objects.requireNonNull(id).toString());
        responseData.put("nickname", authService.getNickname(id));
        responseData.put("profileImg", authService.getProfileImage(id));

        MembersRecordVO recordVO = memberService.getMembersRecord(id);
        responseData.put("total", String.valueOf(recordVO.getMul_total()));
        responseData.put("win", String.valueOf(recordVO.getMul_win()));
        responseData.put("lose", String.valueOf(recordVO.getMul_lose()));
        responseData.put("bestRecord4", String.valueOf(recordVO.getSingle_best_4()));
        responseData.put("cntRecord4", String.valueOf(recordVO.getSingle_cnt_4()));

        return ResponseEntity.ok().body(responseData);
    }

    // 프로필 업데이트
    @PostMapping("UpdateProfile")
    public ResponseEntity<String> UpdateProfile(@RequestParam("nickname") String nickname,
                                                @RequestParam("imgChangeDefault") String imgChangeDefault,
                                                @RequestParam(value = "image", required = false) MultipartFile image) {

        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);

        boolean isInImage = image != null;
        // 파라미터로 이미지가 넘어왔다면 true
        System.out.println("isInImage" + isInImage);

        // 이미지 기본값으로 수정할거면 true
        boolean isChangeDefault = Boolean.parseBoolean(imgChangeDefault);

        // step1. db에 회원 정보 업데이트
        memberService.updateProfile(id, nickname, isInImage, isChangeDefault);

        // step2. 저장하면서 받은 id값을 파일의 이름으로 만들어 s3에 저장
        if (isInImage) {
            System.out.println("이거 시도함?");
            String s3FileName = id.toString();
            String path = s3UploadService.saveWithMultipartFile("user/profileImg/", s3FileName, image);
            if (path == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S3 업로드 중 오류가 발생했습니다.");
            }
        }
        
        // step2. 이미지를 기본값으로 바꿀거면 기존 파일 삭제
        if (isChangeDefault) {
            try {
                String s3FullFilePath = "user/profileImg/" + id.toString();
                s3UploadService.deleteFileFromS3(s3FullFilePath);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("S3 파일삭제 중 오류가 발생했습니다: " + e);
            }
        }

        return ResponseEntity.ok("회원가입 정상처리");
    }

    // 회원탈퇴
    @GetMapping("/withdrawal")
    public ResponseEntity<?> withdrawal() {
        // 헤더에서 토큰 추출
        String authorizationHeader = request.getHeader("Authorization");
        String token = JwtUtil.extractToken(authorizationHeader);
        Integer id = JwtUtil.getID(token);
        // db에서 회원삭제
        memberService.withdrawal(id);
        // s3에서 사진도 삭제
        try {
            String s3FullFilePath = "user/profileImg/" + id.toString();
            s3UploadService.deleteFileFromS3(s3FullFilePath);
        } catch (Exception ignored) {

        }

        return ResponseEntity.ok().body("탈퇴처리 완료");
    }

}
