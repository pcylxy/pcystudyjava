package com.example.provider.service;

import com.pcy.UserService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@DubboService(version = "1.0")
public class UserServiceImpl implements UserService {
    private final static HashMap<String,String> shortMap =new HashMap<>();
    public String getUser( String longUrl){
        if (StringUtils.isEmpty(longUrl)){
            throw new RuntimeException("longUrl is empty");
            /*return "400";*/
        }else {
            String[] split = longUrl.split("/");
            StringBuilder pre = new StringBuilder();
            for (int i = 0; i < split.length - 1; i++) {
                pre.append(split[i]).append("/");
            }
            pre.append(fromBase10(toBase10(split[split.length - 1])));
            System.out.println(longUrl);
            shortMap.put(pre.toString(), longUrl);
            return pre.toString();
        }
    }
    public static final String BASE_62_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int BASE = BASE_62_CHAR.length();
    public static long toBase10(String str) {
        //从右边开始
        return toBase10(new StringBuilder(str).reverse().toString().toCharArray());
    }

    private static long toBase10(char[] chars) {
        long n = 0;
        int pow = 0;
        for(char item: chars){
            n += toBase10(BASE_62_CHAR.indexOf(item),pow);
            pow++;
        }
        return n;
    }

    private static long toBase10(int n, int pow) {
        return n * (long) Math.pow(BASE, pow);
    }
    public static String fromBase10(long i) {
        StringBuilder sb = new StringBuilder("");
        if (i == 0) {
            return "a";
        }
        while (i > 0) {
            i = fromBase10(i, sb);
        }
        return sb.reverse().toString();
    }

    private static long fromBase10(long i, final StringBuilder sb) {
        int rem = (int)(i % BASE);
        sb.append(BASE_62_CHAR.charAt(rem));
        return i / BASE;
    }
}
