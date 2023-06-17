package com.example.duanlianjie1.coltroller;

import java.math.BigInteger;

public class Base62Encoder {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(62);

    public static String encode(String input) {
        BigInteger number = new BigInteger(input.getBytes());
        StringBuilder sb = new StringBuilder();

        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = number.divideAndRemainder(BASE);
            sb.insert(0, CHARACTERS.charAt(divmod[1].intValue()));
            number = divmod[0];
        }

        return sb.toString();
    }

    public static String decode(String encoded) {
        BigInteger number = BigInteger.ZERO;

        for (int i = 0; i < encoded.length(); i++) {
            char c = encoded.charAt(i);
            int digit = CHARACTERS.indexOf(c);
            number = number.multiply(BASE).add(BigInteger.valueOf(digit));
        }

        return new String(number.toByteArray());
    }
}
