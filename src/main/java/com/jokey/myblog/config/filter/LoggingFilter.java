package com.jokey.myblog.config.filter;

import com.jokey.myblog.config.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.filter.AbstractRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 커스텀 로그 필터
 */
@Slf4j
@WebFilter(
        urlPatterns = "/*",
        initParams = {
                @WebInitParam( name = "exceptUrl", value = "/healthCheck,/resources/,/uploads/" ),
                @WebInitParam( name = "includeHeaders", value = "true" ),
                @WebInitParam( name = "includeQueryString", value = "true" ),
                @WebInitParam( name = "includePayload", value = "true" ),
                @WebInitParam( name = "maxPayloadLength", value = "1024" ),
        }
)
@Order( 1 )
public class LoggingFilter extends AbstractRequestLoggingFilter {

    // log message template
    private static final String DEFAULT_BEFORE_REQUEST_MESSAGE_PREFIX = "\n============== JOKEY-BLOG REQUEST BEGIN         ==============\n";
    private static final String DEFAULT_BEFORE_REQUEST_MESSAGE_SUFFIX = "\n============== JOKEY-BLOG REQUEST END           ==============";
    private static final String DEFAULT_AFTER_REQUEST_MESSAGE_PREFIX = "\n============== JOKEY-BLOG REQUEST-PAYLOAD BEGIN ==============\n";
    private static final String DEFAULT_AFTER_REQUEST_MESSAGE_SUFFIX = "\n============== JOKEY-BLOG REQUEST-PAYLOAD END   ==============";
    private static final String DEFAULT_RESPONSE_MESSAGE_PREFIX = "\n============== JOKEY-BLOG RESPONSE BEGIN        ==============\n";
    private static final String DEFAULT_RESPONSE_MESSAGE_SUFFIX = "\n============== JOKEY-BLOG RESPONSE END          ==============";
    private static final String KEY_BEGIN_TIME = "__start_time";
    private static final String KEY_END_TIME = "__end_time";

    private final Marker marker = MarkerFactory.getMarker( "INOUT" );

    private String exceptUrl;
    private String[] exceptUrls;

    // init
    public LoggingFilter() {
        setIncludeHeaders( true );
        setIncludeQueryString( true );
        setIncludePayload( true );
        setMaxPayloadLength( 1024 );
    }

    public String getExceptUrl() {
        return exceptUrl;
    }

    public void setExceptUrl( String exceptUrl ) {
        this.exceptUrl = exceptUrl;
        setExceptUrls( StringUtils.split( this.exceptUrl, "," ) );
    }

    public String[] getExceptUrls() {
        return exceptUrls;
    }

    public void setExceptUrls( String[] exceptUrls ) {
        this.exceptUrls = exceptUrls;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
            throws ServletException, IOException {

        // 리소스는 로깅 대상에서 제외한다
        if( StringUtils.containsAny( request.getServletPath(), this.exceptUrls ) ) {
            filterChain.doFilter( request, response );
            return;
        }

        // check if this is the first request
        boolean isFirstRequest = !isAsyncDispatch( request );
        HttpServletRequest requestToUse = request;
        HttpServletResponse responseToUse = response;

        // 요청 시 Payload 체크
        HttpMethod method = HttpMethod.resolve( request.getMethod() );
        if( method == HttpMethod.POST || method == HttpMethod.PUT ) {
            if( isFirstRequest && !( request instanceof ContentCachingRequestWrapper ) ) {
                requestToUse = new ContentCachingRequestWrapper( request );
            }
        }

        // 응답 시 Payload 체크
        if( StringUtils.indexOf( request.getHeader( HttpHeaders.CONTENT_TYPE ), MediaType.APPLICATION_JSON_VALUE ) != -1 ) {
            if( isFirstRequest && !( response instanceof ContentCachingResponseWrapper ) ) {
                responseToUse = new ContentCachingResponseWrapper( response );
            }
        }

        boolean shouldLog = this.shouldLog( requestToUse );
        if( shouldLog && isFirstRequest ) {
            this.beforeRequest( requestToUse,
                    this.createMessage( requestToUse, DEFAULT_BEFORE_REQUEST_MESSAGE_PREFIX, DEFAULT_BEFORE_REQUEST_MESSAGE_SUFFIX )
            );
        }

        try {
            filterChain.doFilter( requestToUse, responseToUse );
        } finally {
            if( shouldLog && !isAsyncStarted( requestToUse ) ) {
                this.afterRequest( requestToUse, this.createMessage( requestToUse, responseToUse ) );
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createMessage( HttpServletRequest request, String prefix, String suffix ) {
        StringBuilder msg = new StringBuilder();
        msg.append( prefix );
        msg.append( "uri=" ).append( request.getRequestURI() );

        if( isIncludeQueryString() ) {
            String queryString = request.getQueryString();
            if( queryString != null ) {
                msg.append( '?' ).append( queryString );
            }
        }

        if( isIncludeClientInfo() ) {
            String client = request.getRemoteAddr();
            if( StringUtils.length( client ) > 0 ) {
                msg.append( ";client=" ).append( client );
            }
            HttpSession session = request.getSession( false );
            if( session != null ) {
                msg.append( ";session=" ).append( session.getId() );
            }
            String user = request.getRemoteUser();
            if( user != null ) {
                msg.append( ";user=" ).append( user );
            }
        }

        if( isIncludeHeaders() ) {
            msg.append( ";\nheaders=" ).append( new ServletServerHttpRequest( request ).getHeaders() );
        }

        if( isIncludePayload() ) {
            if( request instanceof ContentCachingRequestWrapper ) {
                ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest( request, ContentCachingRequestWrapper.class );
                if( wrapper != null ) {
                    //STICKY
                    if( wrapper.getContentAsByteArray().length == 0 ) {
                        if( StringUtils.indexOf( request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE ) != -1 ) {
                            request.getParameterMap();
                        }
                    }

                    byte[] buf = wrapper.getContentAsByteArray();
                    if( buf.length > 0 ) {
                        int length = Math.min( buf.length, getMaxPayloadLength() );
                        String payload;
                        try {
                            payload = new String( buf, 0, length, wrapper.getCharacterEncoding() );
                        } catch( UnsupportedEncodingException ex ) {
                            payload = "[unknown]";
                        }
                        msg.append( ";\npayload=" ).append( payload );
                    }
                }
            }
        }

        msg.append( suffix );
        return msg.toString();
    }

    /**
     * afterRequest 로그 메세지 생성
     *
     * @param request  ContentCachingRequestWrapper
     * @param response ContentCachingResponseWrapper
     * @return log message
     */
    protected String createMessage( HttpServletRequest request, HttpServletResponse response ) {
        //응답 정보
        StringBuilder msg = new StringBuilder();
        msg.append( DEFAULT_RESPONSE_MESSAGE_PREFIX );
        msg.append( "uri=" ).append( request.getRequestURI() );

        if( response instanceof ContentCachingResponseWrapper ) {

            if( isIncludeHeaders() ) {
                msg.append( ";\nheaders=[" );
                int i = 0;
                for( String name : response.getHeaderNames() ) {
                    if( i++ > 0 ) {
                        msg.append( ", " );
                    }
                    msg.append( name ).append( ":" ).append( response.getHeader( name ) );
                }
                msg.append( "]" );
            }

            if( isIncludePayload() ) {
                //요청 payload
                if( request instanceof ContentCachingRequestWrapper ) {
                    ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest( request, ContentCachingRequestWrapper.class );
                    if( wrapper != null ) {
                        if( StringUtils.indexOf( request.getContentType(), MediaType.APPLICATION_FORM_URLENCODED_VALUE ) == -1 ) {
                            byte[] buf = wrapper.getContentAsByteArray();
                            if( buf.length > 0 ) {
                                int length = Math.min( buf.length, getMaxPayloadLength() );
                                String payload;
                                try {
                                    payload = new String( buf, 0, length, wrapper.getCharacterEncoding() );
                                } catch( UnsupportedEncodingException ex ) {
                                    payload = "[unknown]";
                                }
                                msg.append( ";\nrequest -payload=" ).append( payload );
                            }
                        }
                    }
                }

                // 응답 payload
                ContentCachingResponseWrapper wrapper = WebUtils.getNativeResponse( response, ContentCachingResponseWrapper.class );
                if( wrapper != null ) {
                    byte[] buf = wrapper.getContentAsByteArray();
                    if( buf.length > 0 ) {
                        int length = Math.min( buf.length, getMaxPayloadLength() );
                        String payload;
                        try {
                            payload = new String( buf, 0, length, wrapper.getCharacterEncoding() );
                        } catch( UnsupportedEncodingException ex ) {
                            payload = "[unknown]";
                        }
                        msg.append( ";\nresponse-payload=" ).append( payload );
                        try {
                            wrapper.copyBodyToResponse();
                        } catch( IOException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        msg.append( DEFAULT_RESPONSE_MESSAGE_SUFFIX );
        return msg.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void beforeRequest( HttpServletRequest request, String message ) {
        String messageId = UUID.randomUUID().toString();
        LocalDateTime startTime = LocalDateTime.now();
        request.setAttribute( Constants.KEY_MESSAGE_ID.toString(), messageId );
        request.setAttribute( KEY_BEGIN_TIME, startTime );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" );

        String msg = "REQUEST >>> MESSAGE-ID=[{}], START-TIME=[{}]" + message;
        log.info( marker, msg, messageId, startTime.format( formatter ) );
        log.info( "REQUEST USER AGENT >>> [{}]", request.getHeader( "user-agent" ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterRequest( HttpServletRequest request, String message ) {
        String messageId = ( String ) request.getAttribute( Constants.KEY_MESSAGE_ID.toString() );
        LocalDateTime startTime = ( LocalDateTime ) request.getAttribute( KEY_BEGIN_TIME );
        LocalDateTime endTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss.SSS" );

        String msg = "RESPONSE >>> MESSAGE-ID=[{}], START-TIME=[{}], END-TIME=[{}], RUNNING-TIME=[{}ms]" + message;
        log.info( marker, msg, messageId,
                startTime.format( formatter ), endTime.format( formatter ),
                Duration.between( startTime, endTime ).toMillis() );
    }

}