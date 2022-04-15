package com.swivel.qpon.fileuploader.configurations;

import com.swivel.qpon.fileuploader.util.RequestHeaderLoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ApiHeaderFilter
 */
@WebFilter(urlPatterns = "/api/*")
@Slf4j
public class ApiHeaderFilter implements Filter {

    private static final String APPLICATION_HEADER = "x-api-key";
    private static final String MISSING_API_KEY = "Missing api key";
    private static final String INVALID_API_KEY = "Invalid api key";
    private final String applicationKey;
    private final RequestHeaderLoggerUtil requestHeaderLoggerUtil;

    public ApiHeaderFilter(@Value("${spring.application.api-key}") String applicationKey,
                           RequestHeaderLoggerUtil requestHeaderLoggerUtil) {
        this.applicationKey = applicationKey;
        this.requestHeaderLoggerUtil = requestHeaderLoggerUtil;
    }

    /**
     * This method validates the mandatory x-api-key header.
     *
     * @param servletRequest  servletRequest
     * @param servletResponse servletResponse
     * @param filterChain     filterChain
     * @throws IOException      IOException
     * @throws ServletException ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.debug("Request headers: {}", requestHeaderLoggerUtil.getRequestHeaderMap(request));

        String appKey = request.getHeader(APPLICATION_HEADER);

        if (appKey == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MISSING_API_KEY);
            return;
        }
        if (!appKey.equals(applicationKey)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, INVALID_API_KEY);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

}
