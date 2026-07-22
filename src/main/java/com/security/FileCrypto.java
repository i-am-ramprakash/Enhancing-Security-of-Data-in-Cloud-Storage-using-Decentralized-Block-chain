package com.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/** AES-GCM file encryption with envelope-wrapped per-file data keys. */
public final class FileCrypto {
    private static final int TAG_BITS = 128;
    private static final int NONCE_BYTES = 12;
    private static final SecureRandom RANDOM = new SecureRandom();

    private FileCrypto() { }

    public record EncryptedFile(byte[] ciphertext, byte[] nonce, byte[] wrappedKey,
                                byte[] keyNonce, byte[] ciphertextSha256) { }

    public static EncryptedFile encrypt(byte[] plaintext, SecretKey masterKey) {
        try {
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256, RANDOM);
            SecretKey dataKey = generator.generateKey();
            byte[] fileNonce = nonce();
            byte[] keyNonce = nonce();
            byte[] ciphertext = gcm(Cipher.ENCRYPT_MODE, dataKey, fileNonce, plaintext);
            byte[] wrappedKey = gcm(Cipher.ENCRYPT_MODE, masterKey, keyNonce, dataKey.getEncoded());
            return new EncryptedFile(ciphertext, fileNonce, wrappedKey, keyNonce,
                    MessageDigest.getInstance("SHA-256").digest(ciphertext));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("File encryption failed", e);
        }
    }

    public static byte[] decrypt(EncryptedFile encrypted, SecretKey masterKey) {
        try {
            byte[] rawKey = gcm(Cipher.DECRYPT_MODE, masterKey, encrypted.keyNonce(), encrypted.wrappedKey());
            SecretKey dataKey = new SecretKeySpec(rawKey, "AES");
            return gcm(Cipher.DECRYPT_MODE, dataKey, encrypted.nonce(), encrypted.ciphertext());
        } catch (GeneralSecurityException e) {
            throw new SecurityException("File integrity verification or decryption failed", e);
        }
    }

    public static SecretKey masterKeyFromEnvironment() {
        return keyFromEnvironment("APP_MASTER_KEY");
    }

    public static SecretKey auditKeyFromEnvironment() {
        return keyFromEnvironment("APP_AUDIT_KEY");
    }

    private static SecretKey keyFromEnvironment(String name) {
        String encoded = System.getenv(name);
        if (encoded == null || encoded.isBlank()) {
            throw new IllegalStateException(name + " must be a Base64-encoded 256-bit key");
        }
        byte[] key = Base64.getDecoder().decode(encoded);
        if (key.length != 32) throw new IllegalStateException(name + " must decode to 32 bytes");
        return new SecretKeySpec(key, "AES");
    }

    private static byte[] nonce() {
        byte[] nonce = new byte[NONCE_BYTES];
        RANDOM.nextBytes(nonce);
        return nonce;
    }

    private static byte[] gcm(int mode, SecretKey key, byte[] nonce, byte[] input)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, key, new GCMParameterSpec(TAG_BITS, nonce));
        return cipher.doFinal(input);
    }
}
