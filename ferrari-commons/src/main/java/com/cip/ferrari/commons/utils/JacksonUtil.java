package com.cip.ferrari.commons.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JacksonUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(JacksonUtil.class);

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getInstance() {
        return objectMapper;
    }

    public static String encode(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            LOGGER.error("encode, obj:{}", obj, e);
        }
        return null;
    }

    public static <T> T decode(String jsonStr, Class<T> clazz) {
        try {
            return objectMapper.readValue(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.error("decode, jsonStr:{}, clazz:{}", jsonStr, clazz, e);
        }
        return null;
    }

    public static <T> T decode(String jsonStr, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonStr, typeReference);
        } catch (Exception e) {
            LOGGER.error("decode, jsonStr:{}, typeReference:{}", jsonStr, typeReference, e);
        }
        return null;
    }

}
