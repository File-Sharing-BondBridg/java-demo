package com.example.encryption_demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class EncryptionController {
    
    @Autowired
    private EncryptionService encryptionService;
    
    @PostMapping("/encrypt")
    public ResponseEntity<?> encrypt(@RequestBody TextPayload request) {
        try {
            String ciphertext = encryptionService.encrypt(request.getText());
            return ResponseEntity.ok(new CipherPayload(ciphertext));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Encryption failed: " + e.getMessage()));
        }
    }
    
    @PostMapping("/decrypt")
    public ResponseEntity<?> decrypt(@RequestBody CipherPayload request) {
        try {
            String plaintext = encryptionService.decrypt(request.getCiphertext());
            return ResponseEntity.ok(new TextPayload(plaintext));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Decryption failed: " + e.getMessage()));
        }
    }
}

@RestController
@RequestMapping("/")
class RootController {
    @GetMapping("loaderio-71cde7773e383aa12f1f5d1123308af8/")
    public String loaderioVerification() {
        return "loaderio-71cde7773e383aa12f1f5d1123308af8";
    }
}