package com.brookezb.bhs.security.util;

import io.quarkus.logging.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author brooke_zb
 */
public class CSRFUtil {
    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final byte TOKEN_LENGTH = 16;
    private static final byte SIGNATURE_LENGTH = 32; // Length of SHA-256
    private static final SecureRandom random = new SecureRandom();

    public static String createToken(String secretKey) {
        try {
            Mac sha256 = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
            sha256.init(secret_key);
            byte[] token = new byte[TOKEN_LENGTH];
            random.nextBytes(token);
            byte[] signature = sha256.doFinal(token);
            byte[] result = new byte[TOKEN_LENGTH + SIGNATURE_LENGTH];
            System.arraycopy(token, 0, result, 0, TOKEN_LENGTH);
            System.arraycopy(signature, 0, result, TOKEN_LENGTH, SIGNATURE_LENGTH);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Log.error("Error while creating CSRF token");
            throw new RuntimeException(e);
        }
    }

    public static boolean validateToken(String token, String secretKey) {
        try {
            Mac sha256 = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), HMAC_SHA256);
            sha256.init(secret_key);
            byte[] tokenWithSignature = Base64.getUrlDecoder().decode(token);
            if (tokenWithSignature.length != TOKEN_LENGTH + SIGNATURE_LENGTH) {
                return false;
            }
            byte[] tokenBytes = new byte[TOKEN_LENGTH];
            System.arraycopy(tokenWithSignature, 0, tokenBytes, 0, TOKEN_LENGTH);
            byte[] signatureBytes = new byte[SIGNATURE_LENGTH];
            System.arraycopy(tokenWithSignature, TOKEN_LENGTH, signatureBytes, 0, SIGNATURE_LENGTH);
            byte[] signature = sha256.doFinal(tokenBytes);

            return MessageDigest.isEqual(signature, signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Log.error("Error while validating CSRF token");
            throw new RuntimeException(e);
        }
    }
}
