package com.swivel.qpon.fileuploader.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Request logger utils
 */
@Component
public class RequestHeaderLoggerUtil {

    private static final String MASK_REGEX = "(?<!^.{0,3}).(?!.{0,3}$)";
    private static final String REPLACE_CHARACTER = "*";
    private static final String[] HEADERS_TO_BE_MASKED = {"authorization", "x-api-key"};
    private static final String BEARER_PREFIX = "bearer";
    private final List<String> headerList;

    public RequestHeaderLoggerUtil() {
        headerList = new ArrayList<>(Arrays.asList(HEADERS_TO_BE_MASKED));
    }

    /**
     * Generate request header map
     *
     * @param request request
     * @return header map
     */
    public Map<String, String> getRequestHeaderMap(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            String logValue = headerList.contains(key.toLowerCase()) ? getMaskedValue(value) : value;
            headerMap.put(key, logValue);
        }
        return headerMap;
    }

    /**
     * Get masked value
     *
     * @param value value
     * @return masked value
     */
    private String getMaskedValue(String value) {
        return value.toLowerCase().contains(BEARER_PREFIX) ?
                maskHeaderValue(value.substring(BEARER_PREFIX.length()).trim()) :
                maskHeaderValue(value);
    }

    /**
     * Mask the value
     *
     * @param value value
     * @return masked value
     */
    private String maskHeaderValue(String value) {
        return value.replaceAll(MASK_REGEX, REPLACE_CHARACTER);
    }
}
