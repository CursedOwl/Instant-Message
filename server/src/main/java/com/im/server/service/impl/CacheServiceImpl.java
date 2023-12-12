package com.im.server.service.impl;

import com.im.server.service.CacheService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Function;

@Service
public class CacheServiceImpl implements CacheService {
    @Override
    public <R, ID> R queryWithoutThrough(String KEY_PREFIX, ID id, Class<R> clazz, Duration ttl, Function<ID, R> function) {

        return null;
    }
}
