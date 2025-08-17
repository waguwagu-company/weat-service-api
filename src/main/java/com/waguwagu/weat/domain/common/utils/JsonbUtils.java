package com.waguwagu.weat.domain.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonbUtils {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T parseOrDefault(String json, TypeReference<T> typeRef, T defaultValue) {
        try {
            if (json == null || json.trim().isEmpty() || "null".equalsIgnoreCase(json.trim())) return defaultValue;
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON for " + typeRef.getType() + ": " + json, e);
        }
    }
}
