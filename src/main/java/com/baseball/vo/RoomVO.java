package com.baseball.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomVO {
    private Integer room_id;
    private Integer status; // 1이면 시작함, 0이면 대기중
    private String room_name;
    private Integer time;
    private Integer player1;
    private Integer player2;
    private Integer head; // 방장의 id
}
