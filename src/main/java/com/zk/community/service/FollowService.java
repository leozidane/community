package com.zk.community.service;

import com.zk.community.util.CommunityConstant;
import com.zk.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowService implements CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;
    //关注
    public void follow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                redisTemplate.multi();
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, userId, System.currentTimeMillis());
                return redisTemplate.exec();
            }
        });
    }

    //取消关注
    public void unFollow(int userId, int entityType, int entityId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
                redisTemplate.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, userId);
                return redisTemplate.exec();
            }
        });
    }

    //关注实体的数量
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);
    }

    //粉丝的数量
    public long findFollowerCount(int entityType, int entityId) {
        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }

    //查询当前用户是否关注某个实体
    public boolean hasFollowed(int userId, int entityType, int entityId) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().score(followeeKey, entityId) != null;
    }

    //查询某用户关注的人
    public List<Map<String, Object>> findFolloweeList(int userId, int offset, int limit) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, ENTITY_TYPE_USER);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);
        if (ids == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : ids) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(followeeKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }

    //查询某用户的粉丝
    public List<Map<String, Object>> findFollowerList(int userId, int offset, int limit) {
        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);
        if (ids == null) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Integer id : ids) {
            Map<String, Object> map = new HashMap<>();
            map.put("user", userService.findUserById(id));
            Double score = redisTemplate.opsForZSet().score(followerKey, id);
            map.put("followTime", new Date(score.longValue()));
            list.add(map);
        }
        return list;
    }
}
