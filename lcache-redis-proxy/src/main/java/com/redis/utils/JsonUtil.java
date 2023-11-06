package com.redis.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;
import java.util.Map;

/**
 * @author: jamesyang
 * @date: 2022/9/1
 */
public class JsonUtil {

    //each thread has its own ObjectMapper instance
    private static ThreadLocal<ObjectMapper> objMapperLocal = new ThreadLocal<ObjectMapper>() {
        @Override
        public ObjectMapper initialValue() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.getFactory().disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);// 反序列化多字段，可以正常反序列化
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            return objectMapper;
        }
    };

    public static String toJSON(Object value) {
        String result = null;
        try {
            result = objMapperLocal.get().writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Fix null string
        if ("null".equals(result)) {
            result = null;
        }
        return result;
    }

    public static <T> T toT(String jsonString, Class<T> clazz) {
        if (clazz.isAssignableFrom(String.class)) {
            return (T)jsonString;
        }

       //TODO add other basic type to deserialize
        try {
            return objMapperLocal.get().readValue(jsonString, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    @SuppressWarnings("rawtypes")
    public static <T> T toT(String jsonString, TypeReference<T> valueTypeRef) {
        try {
            return objMapperLocal.get().readValue(jsonString, valueTypeRef);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> toTList(String jsonString, Class<T> clazz) {
        try {
            return objMapperLocal.get().readValue(jsonString, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(String jsonString) {
        return toT(jsonString, Map.class);
    }

    public static String prettyPrint(Object value) {
        String result = null;
        try {
            result = objMapperLocal.get().writeValueAsString(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Fix null string
        if ("null".equals(result)) {
            result = null;
        }
        return result;
    }

}
