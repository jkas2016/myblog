package com.jokey.myblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.MessageSourceAccessor;

@SpringBootApplication
@ServletComponentScan
@Configuration
@PropertySource( value = "classpath:datasource.properties" )
public class MyblogApplication {

    @Bean
    public MessageSourceAccessor messageSourceAccessor( MessageSource messageSource ) {
        return new MessageSourceAccessor( messageSource );
    }

    public static void main( String[] args ) {
        SpringApplication.run( MyblogApplication.class, args );
    }

}
