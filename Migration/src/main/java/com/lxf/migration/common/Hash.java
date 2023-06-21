package com.lxf.migration.common;

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
public class Hash {
    private static final String SHA_256 = "SHA-256";

    public static String getSha256(String str) {
        return hash(str.getBytes(), SHA_256);
    }

    private static String hash(byte[] inputBytes, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(inputBytes);
            byte[] hashBytes = md.digest();
            return bytesToHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to obtain " + algorithm + " instance: " + e.getMessage(), e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}