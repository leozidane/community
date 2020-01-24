package com.zk.community.quartz;

import com.zk.community.entity.DiscussPost;
import com.zk.community.service.DiscussPostService;
import com.zk.community.service.ElasticsearchService;
import com.zk.community.service.LikeService;
import com.zk.community.util.CommunityConstant;
import com.zk.community.util.RedisKeyUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PostScoreRefreshJob implements Job , CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(PostScoreRefreshJob.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private LikeService likeService;

    private static final Date epoch;

    static {
        try {
            epoch = new SimpleDateFormat("yyyy-MM-dd").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
           throw new RuntimeException("初始化网站成立时间失败");
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0) {
            logger.info("任务取消，没有需要更新的帖子");
            return;
        }
        logger.info("任务开始，正在刷新帖子分数：" + operations.size());
        while(operations.size() > 0) {
            this.refresh((Integer) operations.pop());
        }
        logger.info("任务结束，帖子分数刷新完毕!");
    }

    private void refresh(int postId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            logger.error("该帖子不存在，id = " + postId);
            return;
        }

        //是否加精
        boolean wonderful = post.getStatus() == 1;
        //评论数
        int commentCount = post.getCommentCount();
        //点赞数
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        //计算权重
        double power = (wonderful ? 75 : 0) + commentCount * 10 + likeCount * 2;
        //分数 = 权重 + 距离天数
        double score = Math.log10(Math.max(power, 1))
                + (post.getCreateTime().getTime() - epoch.getTime()) / (1000 * 3600 * 24);
        //更新帖子分数
        discussPostService.updateScore(post.getId(), score);
        //同步搜索数据
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}
