package com.jokey.myblog.biz.model.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity( name = "tb_user" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class UserVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long key;
    @Column( nullable = false )
    private String name;
    @Column( nullable = false )
    private String password;
    @Column( nullable = false )
    private String nickname;
    private String email;
    @Column( nullable = false )
    private int loginFailCnt;
    private LocalDate lastLoginDate;
    @Column( nullable = false )
    private LocalDateTime regDatetime;
    private LocalDateTime modDatetime;

}
