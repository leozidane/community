package com.zk.community.service;

import com.zk.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定ip计入UV
    public void recordUV(String ip) {
        String redisKey = RedisKeyUtil.getUVKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey, ip);
    }

    //统计指定范围内的UV
    public long calculateUV(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (start.after(end)) {
            return 0;
        }
        //得到该日期范围内的所有key
        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String redisKey = RedisKeyUtil.getUVKey(df.format(calendar.getTime()));
            keyList.add(redisKey);
            //日期加1
            calendar.add(Calendar.DATE, 1);
        }

        //合并数据
        String unionKey = RedisKeyUtil.getUVKey(df.format(start), df.format(end));
        redisTemplate.opsForHyperLogLog().union(unionKey, keyList.toArray());
        //返回结果
        return redisTemplate.opsForHyperLogLog().size(unionKey);
    }

    //将指定用户计入到DAU中
    public void recordDAU(int userId) {
        String redisKey = RedisKeyUtil.getDAUKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey, userId, true);
    }

    //统计指定日期内的DAU
    public long calculateDAU(Date start, Date end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        if (start.after(end)) {
            return 0;
        }
        //得到该日期范围内的所有key
        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) {
            String redisKey = RedisKeyUtil.getDAUKey(df.format(calendar.getTime()));
            keyList.add(redisKey.getBytes());
            //日期加1
            calendar.add(Calendar.DATE, 1);
        }

        //返回结果
        return (long) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                //进行or运算,合并数据
                String unionKey = RedisKeyUtil.getDAUKey(df.format(start), df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                       unionKey.getBytes(), keyList.toArray(new byte[0][0]));
                return connection.bitCount(unionKey.getBytes());
            }
        });
    }
}
