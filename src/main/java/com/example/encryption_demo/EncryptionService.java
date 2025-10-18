package com.example.encryption_demo;

import org.springframework.stereotype.Service;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

@Service
public class EncryptionService {
    
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH = 128; // 16 bytes
    private static final int IV_LENGTH = 12;   // 12 bytes for GCM
    
    // Same key as your Go/Python versions
    private static final byte[] KEY = "0123456789abcdef0123456789abcdef".getBytes();
    private final SecretKey secretKey;
    private final SecureRandom secureRandom;
    
    public EncryptionService() {
        this.secretKey = new SecretKeySpec(KEY, "AES");
        this.secureRandom = new SecureRandom();
    }
    
    public String encrypt(String plaintext) throws Exception {
        // Generate random IV (Initialization Vector)
        byte[] iv = new byte[IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        // Initialize cipher in encryption mode
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        // Encrypt the text
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes());
        
        // Combine IV + ciphertext
        byte[] combined = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);
        
        // Return base64 encoded result
        return Base64.getEncoder().encodeToString(combined);
    }
    
    public String decrypt(String base64Ciphertext) throws Exception {
        // Decode from base64
        byte[] combined = Base64.getDecoder().decode(base64Ciphertext);
        
        if (combined.length < IV_LENGTH) {
            throw new IllegalArgumentException("Invalid ciphertext");
        }
        
        // Extract IV and ciphertext
        byte[] iv = new byte[IV_LENGTH];
        byte[] ciphertext = new byte[combined.length - IV_LENGTH];
        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, ciphertext, 0, ciphertext.length);
        
        // Initialize cipher in decryption mode
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        // Decrypt the text
        byte[] plaintext = cipher.doFinal(ciphertext);
        return new String(plaintext);
    }
}