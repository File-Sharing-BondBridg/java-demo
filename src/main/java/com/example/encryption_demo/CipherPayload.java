package com.example.encryption_demo;

public class CipherPayload {
    private String ciphertext;
    
    public CipherPayload() {}
    
    public CipherPayload(String ciphertext) {
        this.ciphertext = ciphertext;
    }
    
    public String getCiphertext() { return ciphertext; }
    public void setCiphertext(String ciphertext) { this.ciphertext = ciphertext; }
}