package com.baseball.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("membersRecord")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MembersRecordVO {
    private int id;
    private int mul_total;
    private int mul_win;
    private int mul_lose;
    private int single_best_4;
    private int single_cnt_4;
}
