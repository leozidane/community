package com.zk.community;

import com.zk.community.entity.DiscussPost;
import com.zk.community.service.DiscussPostService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.context.SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SpringBootTest {

    @Autowired
    private DiscussPostService discussPostService;

    private DiscussPost data;

    @BeforeClass
    public static void BeforeClass() {
        System.out.println("BeforeClass");
    }

    @AfterClass
    public static void AfterClass() {
        System.out.println("AfterClass");
    }

    @Before
    public void before() {
        System.out.println("before");
        //初始化数据
        data = new DiscussPost();
        data.setTitle("Test Title");
        data.setContent("Test Content");
        data.setCreateTime(new Date());
        discussPostService.addDiscussPost(data);
    }

    @After
    public void After() {
        System.out.println("after");
        discussPostService.updateStatus(data.getId(), 2);
    }

    @Test
    public void testFindById() {
        DiscussPost post = discussPostService.findDiscussPostById(data.getId());
        Assert.assertNotNull(post);
        Assert.assertEquals(post.toString(), data.toString());
    }

    @Test
    public void testUpdateScore() {
        int row = discussPostService.updateScore(data.getId(), 2000);
        Assert.assertEquals(1, row);
        double score = discussPostService.findDiscussPostById(data.getId()).getScore();
        Assert.assertEquals(2000.00, score, 2);
    }
}
