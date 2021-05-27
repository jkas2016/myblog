package com.jokey.myblog.biz.model.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "tb_board" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BoardVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long key;
    @Column( nullable = false )
    private String title;
    @Column( nullable = false, columnDefinition = "TEXT" )
    private String content;
    @Column( nullable = false )
    private LocalDateTime regDatetime;
    private LocalDateTime modDatetime;
    private long categoryKey;
    @Column( nullable = false )
    private long userKey;

}
