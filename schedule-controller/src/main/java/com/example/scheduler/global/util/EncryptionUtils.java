package com.example.scheduler.global.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtils {

    private static final String ALGORITHM   = "AES/GCM/NoPadding";
    private static final String KEY_FACTORY = "PBKDF2WithHmacSHA256";
    private static final int    KEY_LENGTH  = 256;
    private static final int    ITERATIONS  = 65536;
    private static final int    GCM_TAG_BIT = 128;
    private static final int    IV_LENGTH   = 12;
    private static final byte[] SALT        = "SchedulerSalt!@#".getBytes();

    public static String encrypt(String plainText, String secretKey) throws Exception {
        SecretKey key = deriveKey(secretKey);

        byte[] iv = new byte[IV_LENGTH];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BIT, iv));

        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        // iv + encrypted 합쳐서 Base64 인코딩
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        SecretKey key = deriveKey(secretKey);

        byte[] combined = Base64.getDecoder().decode(encryptedText);

        byte[] iv        = new byte[IV_LENGTH];
        byte[] encrypted = new byte[combined.length - IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, encrypted, 0, encrypted.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_BIT, iv));

        return new String(cipher.doFinal(encrypted));
    }

    private static SecretKey deriveKey(String secretKey) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(
                secretKey.toCharArray(), SALT, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(KEY_FACTORY);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }
}