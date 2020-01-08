package com.zk.community;

import com.zk.community.dao.DiscussPostMapper;
import com.zk.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class DiscussMapperTest {
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectDiscussPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 5);
        for (DiscussPost post : discussPosts) {
            System.out.println(post);
        }
    }

    @Test
    public void testSelectDiscussPostRow() {
        int i = discussPostMapper.selectDiscussPostRow(0);
        System.out.println(i);
    }
}
