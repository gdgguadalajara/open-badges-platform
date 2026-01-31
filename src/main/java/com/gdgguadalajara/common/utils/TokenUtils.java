package com.gdgguadalajara.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TokenUtils {

    public static String encodeToken(String email, String code) {
        String raw = email + ":" + code;
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * [0] = email, [1] = code
     * @param token
     * @return
     */
    public static String[] decodeToken(String token) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(token);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        return decodedString.split(":");
    }
}
