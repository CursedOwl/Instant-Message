package com.im.server.service;

import java.time.Duration;
import java.util.function.Function;

public interface CacheService {

    public <R,ID>R queryWithoutThrough(String KEY_PREFIX, ID id, Class<R> clazz, Duration ttl, Function<ID,R> function);
}
