package com.baseball.service;

import com.baseball.dao.AuthDao;
import com.baseball.dao.MemberDao;
import com.baseball.vo.MembersRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private AuthDao authDao;

    // 게임기록 가져오기
    public MembersRecordVO getMembersRecord(Integer id) {
        return memberDao.getMembersRecord(id);
    }
    
    // 유저 정보 수정
    public void updateProfile(Integer id, String nickname, boolean isInImage, boolean isChangeDefault) {
        memberDao.updateProfile(id, nickname);
        // 넘어온 사진이 있으면 수정 (디폴트사진에서 사진추가가 되었을 수 있으니)
        if (isInImage) {
            authDao.updateProfileImg(id, id.toString());
        }
        // 기본사진으로 수정
        if (isChangeDefault) {
            authDao.updateProfileImg(id, "default");
        }
    }

    // 유저 탈퇴
    public void withdrawal(Integer id) {
        memberDao.withdrawal(id);
    }
}
