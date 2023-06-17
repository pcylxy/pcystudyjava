package com.example.duanlianjie1.coltroller;
import com.google.common.hash.Hashing;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
@RestController
public class javacontroller {
    private final static HashMap<String,String> shortMap =new HashMap<>();
    @GetMapping("/1")
    public String long2short(@RequestParam("str") String longstring){
        String[] split = longstring.split("/");
        StringBuilder pre=new StringBuilder();
        for (int i = 0; i < split.length-1; i++) {
            pre.append(split[i]).append("/");
        }
        pre.append(Base62Encoder.encode(split[split.length-1]));
        shortMap.put(pre.toString(),longstring);
        return pre.toString();
    }
    @GetMapping("/2")
    public String selectLong(@RequestParam("str") String shortString){
        if (shortMap.containsKey(shortString)){
            return shortMap.get(shortString);
        }else {
            return "-1";
            /*throw new RuntimeException();*/
        }
    }
    @GetMapping("/{id}")
    public String select(@PathVariable int id){
        System.out.println(id);
        return "0";
    }


}
