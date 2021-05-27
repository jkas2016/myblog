package com.jokey.myblog.biz.model.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Entity( name = "tb_board_file_mapping" )
@Data
@JsonInclude( JsonInclude.Include.NON_NULL )
public class BoardFileMappingVO {

    @EmbeddedId
    private BoardFilePK boardFilePK;

}
