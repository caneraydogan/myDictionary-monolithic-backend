package com.caner.interceptor;

import com.caner.bean.RequestLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.caner.config.CoreConstants.X_AUTH_TOKEN;
import static com.caner.config.CoreConstants.X_FORWARDED_FOR;

/**
 * Http logging filter, which wraps around request and response in
 * each http call and logs
 * whole request and response bodies. It is enabled by
 * putting this instance into filter chain
 * by overriding getServletFilters() in
 * AbstractAnnotationConfigDispatcherServletInitializer.
 */
public class LoggingFilter extends AbstractRequestLoggingFilter {

    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);


    @Value("${spring.application.name}")
    private String applicationName;

    //    @Qualifier("mainObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();
        RequestLoggingWrapper requestLoggingWrapper = new RequestLoggingWrapper(startTime, request);
        ResponseLoggingWrapper responseLoggingWrapper = new ResponseLoggingWrapper(startTime, response);

        super.doFilterInternal(requestLoggingWrapper, responseLoggingWrapper, filterChain);

        LocalDateTime localDate = LocalDateTime.now(ZoneId.systemDefault());

        RequestLog requestLog = new RequestLog();

        requestLog.setLogSource("LF");
        requestLog.setRequestDate(localDate);
        requestLog.setRequestMethod(request.getMethod());
        requestLog.setPath(request.getPathInfo());
        requestLog.setxAuthToken(request.getHeader(X_AUTH_TOKEN));
        requestLog.setApplicationName(applicationName);
        requestLog.setResponseStatus(response.getStatus());

        String ipAddress = request.getHeader(X_FORWARDED_FOR);
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        requestLog.setClientIp(ipAddress);

        Map<String, String> map = new HashMap<>();

        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }

        requestLog.setRequestHeader(objectMapper.writeValueAsString(map));
        requestLog.setRequestBody(requestLoggingWrapper.getRequestBody());


        map = new HashMap<>();

        Collection<String> responseHeaderNames = response.getHeaderNames();
        for (String headerName : responseHeaderNames) {
            map.put(headerName, response.getHeader(headerName));
        }

        requestLog.setResponseHeader(objectMapper.writeValueAsString(map));
        requestLog.setResponseBody(responseLoggingWrapper.getResponseBody());
        requestLog.setMessageDirection("INCOMING");

        long elapsedTime = System.currentTimeMillis() - startTime;
        requestLog.setElapsedTime(elapsedTime);

        logger.info("response status code: {}, request method: {}, request URI: {}, request headers: {},request Body: {},  response headers: {},response Body: {}",
                response.getStatus(),
                request.getMethod(),
                request.getRequestURI(),
                requestLog.getRequestHeader(),
                requestLog.getRequestBody(),
                requestLog.getResponseHeader(),
                requestLog.getResponseBody()
        );
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
    }
}
