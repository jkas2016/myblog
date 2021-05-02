package com.jokey.myblog.test.mapper;

import com.jokey.myblog.test.model.TestVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * tb_test curd mapper
 */
public interface TestMapper extends JpaRepository<TestVO, Long>, JpaSpecificationExecutor<TestVO> {
}
