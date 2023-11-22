package com.im.server.util;

import cn.hutool.core.date.DateTime;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;

import java.util.Calendar;
import java.util.HashMap;

@Slf4j
public class TokenUtil {

    public static String create(String secret,String account){

        HashMap<String,Object> header = new HashMap<>();
        header.put("alg","HS256");
        header.put("typ","JWT");

        HashMap<String,Object> payload = new HashMap<>();
        payload.put("sub","tcp_connect");
        payload.put("account",String.valueOf(account));
        payload.put("admin",false);

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DAY_OF_WEEK,1);

        return Jwts.builder()
                .setHeader(header)
                .setClaims(payload)
                .setExpiration(instance.getTime())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public static Tuple2<Boolean,String> verify(String token) {String secret="HfkjksFKLJISJFKLFKWJFQFIQWIOFJQOFFQGGSDGFFJIQOEUFIEJFIOQWEFHFQOK5FKOIQWUFFEFE423FIQEOFJHUEWHFKASKDLQWJIFSJDJKFHJIJWO";

        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            Claims body = claimsJws.getBody();

            if (body.getExpiration().before(DateTime.now())) {
                log.warn("Token Expired:"+token);
                return Tuple2.of(false,null);
            }
            log.info("Token Accepted:"+token);
            return Tuple2.of(true,body.get("account",String.class));

        } catch (Exception e) {
            log.warn("Token Invalid:"+token);
            e.printStackTrace();
            return Tuple2.of(false,null);
        }

    }
}
