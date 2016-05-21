package ru.spbau.mit;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public final class MD5Calculator {
    private MD5Calculator() {
    }

    public static String getMD5String(String s) throws Exception {
        return calcMD5(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)));
    }

    public static String getMD5File(String filename) throws Exception {
        return calcMD5(new FileInputStream(filename));
    }

    private static String calcMD5(InputStream fis) throws Exception {
        byte[] checksum = createChecksum(fis);
        StringBuilder result = new StringBuilder();

        for (byte b : checksum) {
            result.append(Integer.toHexString(b & 0xFF));
        }
        return result.toString();
    }

    private static byte[] createChecksum(InputStream fis) throws Exception {
        DigestInputStream dis = new DigestInputStream(fis, MessageDigest.getInstance("MD5"));
        return dis.getMessageDigest().digest();
    }

}
