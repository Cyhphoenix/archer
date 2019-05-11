package com.blog.archer.util;

import com.blog.archer.exception.UnknownAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

/**
 * @author yuanhang
 */
public class EncryptUtil {

    public static final String ALGO_MD5 = "MD5";

    public static final String ALG0_SHA1 = "SHA-1";

    public static final String UTF_8 = "UTF-8";

    public static final byte[] TIME_SALT = Instant.now().toString().getBytes();

    /**
     * @param algorithm
     * @param source
     * @return
     * @throws UnknownAlgorithmException
     */
    public static String encode(String algorithm, String source) throws UnknownAlgorithmException {
        if (StringUtils.isBlank(source)) {
            return StringUtils.EMPTY;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hashed = digest.digest(source.getBytes(Charset.forName(UTF_8)));
            return Hex.encodeHexString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new UnknownAlgorithmException("No such algorithm [" + algorithm + "]");
        }
    }

    /**
     * 加盐算法
     *
     * @param algorithm
     * @param bytes
     * @param salt
     * @return
     * @throws UnknownAlgorithmException
     */
    public static String encode(String algorithm, byte[] bytes, byte[] salt) throws UnknownAlgorithmException {
        return encode(algorithm, bytes, salt, 1);
    }

    /**
     * 加盐算法
     *
     * @param bytes
     * @param salt
     * @param hashIterations
     * @return
     * @throws UnknownAlgorithmException
     */
    public static String encode(String algorithm, byte[] bytes, byte[] salt, int hashIterations) throws UnknownAlgorithmException {
        if (bytes == null) {
            return StringUtils.EMPTY;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.reset();
                digest.update(salt);
            }

            byte[] hashed = digest.digest(bytes);

            hashIterations = Math.max(1, hashIterations);
            int iterations = hashIterations - 1;

            for (int i = 0; i < iterations; ++i) {
                digest.reset();
                hashed = digest.digest(hashed);
            }
            return Hex.encodeHexString(hashed);

        } catch (NoSuchAlgorithmException e) {
            throw new UnknownAlgorithmException("No such algorithm [" + algorithm + "]");
        }
    }

}
