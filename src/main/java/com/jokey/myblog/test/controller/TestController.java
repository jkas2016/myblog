package com.jokey.myblog.test.controller;

import com.jokey.myblog.test.model.TestVO;
import com.jokey.myblog.test.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping( value = "/test" )
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class TestController {

    private final TestService testService;

    @GetMapping(
            value = "/read",
            produces = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE },
            consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE }
    )
    public ResponseEntity<Object> read() {

        List<TestVO> list = testService.selectTestList();

        return new ResponseEntity<>( list, HttpStatus.OK );
    }
}
