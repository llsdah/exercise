package com.example;

import com.example.scheduler.global.util.EncryptionUtils;

public class EncryptionGenerator {

    public static void main(String[] args) throws Exception {
        String secretKey  = "wfw";   // 환경변수로 사용할 키
        String plainText  = "sa";  // 암호화할 평문

        String encrypted  = EncryptionUtils.encrypt(plainText, secretKey);
        String decrypted  = EncryptionUtils.decrypt(encrypted, secretKey);

        System.out.println("Plain    : " + plainText);
        System.out.println("Encrypted: ENC(" + encrypted + ")");
        System.out.println("Decrypted: " + decrypted);
    }
}