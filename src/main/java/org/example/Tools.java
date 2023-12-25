package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import io.github.kosssst.asymcryptolab1.generators.L20Generator;
import main.util.TextUtil;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Tools {
    public static String hash(String text) {
        byte[] bytes = new BigInteger(text, 16).toByteArray();
        return DigestUtils.sha1Hex(bytes);
    }

    public static String getRandomBytes(int bits) {
        SecureRandom random = new SecureRandom();
        return TextUtil.bitsToBytes(random.ints(bits, 0, 2).mapToObj(Integer::toString).collect(Collectors.joining())).toLowerCase();
    }

    public static Map<String, String> getRedundancyTable(int K, int L, int truncation, RedundancyFunction function) {
        Map<String, String> redundancyTable = new HashMap<>();
        long l = (long) Math.pow(2, L);
        long k = (long) Math.pow(2, K);

        for (long i = 0L; i < k; i++) {
            String x0 = Tools.getRandomBytes(truncation);
            String xPrev = x0;
            for (long j = 0L; j < l; j++) {
                xPrev = Tools.hash(function.get(xPrev)).substring(36);
            }
            redundancyTable.put(x0, xPrev);
        }

        return redundancyTable;
    }
}
