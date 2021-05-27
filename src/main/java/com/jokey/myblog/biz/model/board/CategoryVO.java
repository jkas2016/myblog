package com.jokey.myblog.biz.model.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "tb_category" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class CategoryVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long key;
    @Column( nullable = false )
    private String name;
    @Column( columnDefinition = "TEXT" )
    private String description;
    @Column( nullable = false )
    private LocalDateTime regDatetime;
    private LocalDateTime modDatetime;
    private long parentCategoryKey;
    @Column( nullable = false )
    private long userKey;

}
