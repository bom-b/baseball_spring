package com.baseball.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SingleGameDao {

    void updateSingleCnt4(@Param("id") Integer id);
    void updateSingleBest4(@Param("id") Integer id, @Param("single_best_4") Integer turn);

}
