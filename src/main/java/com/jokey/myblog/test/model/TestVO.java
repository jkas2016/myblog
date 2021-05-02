package com.jokey.myblog.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity( name = "tb_test" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class TestVO {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private long idx;
    @Column( nullable = false, columnDefinition = "VARCHAR(10)" )
    private String name;
    @Column( columnDefinition = "VARCHAR(50)" )
    private String description;
    @Column( nullable = false, columnDefinition = "DATETIME" )
    private LocalDateTime regDatetime = LocalDateTime.now();

}
