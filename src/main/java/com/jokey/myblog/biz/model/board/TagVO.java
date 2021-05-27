package com.jokey.myblog.biz.model.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "tb_tag" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class TagVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long key;
    @Column( nullable = false )
    private String name;
    @Column( nullable = false )
    private LocalDateTime regDatetime;
    @Column( nullable = false )
    private long userKey;

}
