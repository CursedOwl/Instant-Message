package com.im.server.Interceptor;

import com.im.server.util.TokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TokenInterceptor implements HandlerInterceptor {

    private String secret;

    public TokenInterceptor(String secret) {
        this.secret = secret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info(request.getRequestURI());

        String authorization = request.getHeader("authorization");
        if(authorization==null){
            log.warn("TokenInterceptor: authorization is null");
            return false;
        }
        Tuple2<Boolean, String> verify = TokenUtil.verify(authorization);
        if (!verify.f0) {
            return false;
        }

        response.setHeader("authorization",TokenUtil.create(secret, verify.f1));
        return true;

    }

}
