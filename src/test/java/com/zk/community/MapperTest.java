package com.zk.community;

import ch.qos.logback.classic.spi.EventArgUtil;
import com.zk.community.dao.*;
import com.zk.community.entity.*;
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

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MessageMapper messageMapper;

//    @Test
//    public void testSelectDiscussPosts() {
//        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(149, 0, 5);
//        for (DiscussPost post : discussPosts) {
//            System.out.println(post);
//        }
//    }

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

    @Test
    public void testInsertDiscussPost() {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(11);
        discussPost.setContent("2123");
        discussPost.setCreateTime(new Date());
        discussPostMapper.insertDiscussPost(discussPost);
    }

    @Test
    public void testSelectDiscussPostById() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(276);
        System.out.println(discussPost);
    }

    @Test
    public void testSelectCommentsByEntity() {
        List<Comment> comments = commentMapper.selectCommentsByEntity(1, 228, 0, 10);
        System.out.println(comments);
    }

    @Test
    public void testSelectCountByEntity() {
        int i = commentMapper.selectCountByEntity(1, 228);
        System.out.println(i);
    }

    @Test
    public void testSelectConversations() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        System.out.println(messages);
    }

    @Test
    public void testSelectConversationCount() {
        int i = messageMapper.selectConversationCount(111);
        System.out.println(i);
    }

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectLetters("111_112", 0, 20);
        System.out.println(messages);
    }

    @Test
    public void testSelectLetterCount() {
        int i = messageMapper.selectLetterCount("111_112");
        System.out.println(i);
    }

    @Test
    public void testSelectLetterUnreadCount() {
        System.out.println(messageMapper.selectLetterUnreadCount(111, null));
    }
}
