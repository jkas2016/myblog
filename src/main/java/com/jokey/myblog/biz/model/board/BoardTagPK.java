package com.jokey.myblog.biz.model.board;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BoardTagPK implements Serializable {

    private static final long serialVersionUID = 1L;
    private long boardKey;
    private long tagKey;

}
