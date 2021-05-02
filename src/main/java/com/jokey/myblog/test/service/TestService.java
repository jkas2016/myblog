package com.jokey.myblog.test.service;

import com.jokey.myblog.common.dao.CustomSpecifications;
import com.jokey.myblog.common.dao.SearchCriteria;
import com.jokey.myblog.test.mapper.TestMapper;
import com.jokey.myblog.test.model.TestVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Test;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final TestMapper testMapper;

    /**
     * JPA 검색 조회 샘플
     * @return tb_test 전체 리스트
     */
    public List<TestVO> selectTestList() {
        CustomSpecifications<TestVO> specifications = new CustomSpecifications<>();
        List<SearchCriteria<?>> list = new ArrayList<>();
        list.add( new SearchCriteria<>( "name", "test", SearchCriteria.SearchOperation.MATCH ) );
        specifications.add( list );

        return testMapper.findAll( specifications );
    }
}
