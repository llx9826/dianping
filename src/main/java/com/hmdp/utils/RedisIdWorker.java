package com.hmdp.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class RedisIdWorker {
    private static final long BEGIN_DATE_STAMP = 1640995200L;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public long nextId(String keyPrefix) {
        //1.生成时间戳
        LocalDateTime localDateTime = LocalDateTime.now();
        long now = localDateTime.toEpochSecond(ZoneOffset.UTC);
        Long timestamp = now - BEGIN_DATE_STAMP;
        //2.生成序列号
        //2.1.获取当天日期,根据天数制作key
        String date = localDateTime.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        //2.2生成
        Long increment = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
        System.out.println(timestamp);
        //3.拼接
        return timestamp << 32 | increment;
    }
}
