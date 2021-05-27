package com.jokey.myblog.biz.model.etc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "tb_file" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class FileVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long key;
    @Column( nullable = false )
    private String fileName;
    @Column( nullable = false )
    private LocalDateTime regDatetime;
    @Column( nullable = false )
    private long userKey;

}
