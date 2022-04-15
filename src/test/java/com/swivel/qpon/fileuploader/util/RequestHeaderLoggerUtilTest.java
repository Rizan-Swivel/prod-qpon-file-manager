package com.swivel.qpon.fileuploader.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RequestHeaderLoggerUtilTest {

    private final RequestHeaderLoggerUtil requestHeaderLoggerUtil = new RequestHeaderLoggerUtil();

    @Test
    void Should_Mask_BearerTokenHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer 42b5bc9c-fddb-4202-bcbb-d7677e0a0316");
        request.addHeader("accept", "application/json");
        Map<String, String> stringStringMap = requestHeaderLoggerUtil.getRequestHeaderMap(request);
        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
            if (entry.getKey().equals("Authorization"))
            assertEquals("42b5****************************0316", entry.getValue());
        }
    }

    /**
     * api-key not registered in mask header list
     */
    @Test
    void Should_Mask_ApiKeyHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-API-Key", "tokoin-key-342b5bc9c-fddb-4202-bcbb-d7677e0a0316");
        Map<String, String> stringStringMap = requestHeaderLoggerUtil.getRequestHeaderMap(request);
        for (Map.Entry<String, String> entry : stringStringMap.entrySet()) {
            assertEquals("toko****************************************0316", entry.getValue());
        }
    }

}
