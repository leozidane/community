package com.zk.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString() {
        String key = "test:user";
        redisTemplate.opsForValue().set(key, 1);
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));
    }

    @Test
    public void testHash() {
        String key = "test:student";
        redisTemplate.opsForHash().put(key, "id", 1);
        redisTemplate.opsForHash().put(key,"name", "zk");
        System.out.println(redisTemplate.opsForHash().get(key, "id"));
        System.out.println(redisTemplate.opsForHash().get(key, "name"));
        System.out.println(redisTemplate.opsForHash().size(key));
    }
    //多次访问同一个key
    @Test
    public void testBoundOperation() {
        String key = "test:user";
        BoundValueOperations operations = redisTemplate.boundValueOps(key);
        operations.increment();
        operations.increment();
        operations.increment();
        System.out.println(operations.get());
    }
    //编程式事务
    @Test
    public void testTransactional() {

        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String key = "test:tx";
                redisOperations.multi();
                redisOperations.opsForSet().add(key, "zhangsan");
                redisOperations.opsForSet().add(key, "zk");
                redisOperations.opsForSet().add(key, "zhangyw");
                System.out.println(redisOperations.opsForSet().members(key));
                return redisOperations.exec();
            }
        });
        System.out.println(execute);
    }
}
