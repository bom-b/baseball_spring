package com.baseball.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Alias("member")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberVO {

    private int nnum;
    private String id;
    private String email;
    private String password;
    private String name;
    private String gender;

}