package org.example;

import io.github.kosssst.asymcryptolab1.generators.L20Generator;
import main.util.TextUtil;

public class RedundancyFunction {
    private String r;

    public RedundancyFunction(int n) {
        L20Generator generator = new L20Generator();
        r = TextUtil.bitsToBytes(generator.generate(n));
    }

    public String get(String x) {
        return r + x;
    }
}
