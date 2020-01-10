package com.zk.community;

import com.zk.community.dao.DiscussPostMapper;
import com.zk.community.dao.LoginTicketMapper;
import com.zk.community.dao.UserMapper;
import com.zk.community.entity.DiscussPost;
import com.zk.community.entity.LoginTicket;
import com.zk.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

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

    @Test
    public void testSelectById() {
        User user = userMapper.selectById(149);
        System.out.println(user);
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 10));
        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("81326551395040da8f30da2dfc464126");
        System.out.println(loginTicket);
    }

    @Test
    public void testUpdateLoginTicket() {
        int abc = loginTicketMapper.updateStatus("abc", 1);
        System.out.println(abc);
    }
}
