package com.baseball.service;

import com.baseball.dao.SingleGameDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SingleGameService {

    @Autowired
    private SingleGameDao singleGameDao;

    // 게임 횟수 업데이트와 최고기록 업데이트
    public void updateRecord4(Integer id, Integer turn) {
        singleGameDao.updateSingleCnt4(id);
        singleGameDao.updateSingleBest4(id, turn);
    };
}
