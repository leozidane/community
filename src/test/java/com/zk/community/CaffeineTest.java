package com.zk.community;

import com.zk.community.entity.DiscussPost;
import com.zk.community.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CaffeineTest {
    @Autowired
    private DiscussPostService discussPostService;

    @Test
    public void init() {
        for (int i = 1; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("互联网暖春计划");
            post.setContent("今年的就业形势，确实不容乐观。过了个年，仿佛跳水一般，整个讨论区哀鸿遍野！19届真的没人要了吗？！18届被优化真的没有出路了吗？！大家的“哀嚎”与“悲惨遭遇”牵动了每日潜伏于讨论区的牛客小哥哥小姐姐们的心，于是牛客决定：是时候为大家做点什么了！为了帮助大家度过“艰难”，牛客网特别联合60+家企业，开启互联网求职暖春计划，面向18届&19届，拯救0 offer！");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 2000);
            discussPostService.addDiscussPost(post);
        }
    }
    @Test
    public void testPostListCache() {
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(discussPostService.findDiscussPosts(0, 0, 10, 0));
    }
}
