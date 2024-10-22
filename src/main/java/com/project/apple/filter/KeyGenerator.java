package com.project.apple.filter;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {
    public static void main(String[] args) {
        String prefix = "nguyennpcoderJWT";
        int prefixLength = prefix.length();
        int totalLength = 32; // Total length required for HS256

        // Ensure the random part fills the remaining length
        byte[] randomBytes = new byte[totalLength - prefixLength];
        SecureRandom random = new SecureRandom();
        random.nextBytes(randomBytes);
        String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        // Combine prefix with random part
        String finalKey = prefix + randomPart;

        // Trim to ensure the key is exactly 32 bytes
        finalKey = finalKey.substring(0, totalLength);

        System.out.println("Generated Key: " + finalKey);
    }
}
