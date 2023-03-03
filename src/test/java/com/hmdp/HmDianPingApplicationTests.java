package com.hmdp;

import com.hmdp.utils.RedisIdWorker;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest
@RunWith(SpringRunner.class)
class HmDianPingApplicationTests {

    @Resource
    private RedisIdWorker redisIdWorker;


    @org.junit.jupiter.api.Test
    void test1() {
        long ceshi = redisIdWorker.nextId("wuliao2");
        System.out.println(ceshi);
    }
}
