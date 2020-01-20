package com.zk.community.service;

import com.zk.community.dao.CommentMapper;
import com.zk.community.entity.Comment;
import com.zk.community.util.CommunityConstant;
import com.zk.community.util.CommunityUtil;
import com.zk.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCountByEntity(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
    /**
     * 增加评论
     * @param comment
     * @return
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("请求参数为空");
        }

        //转义HTML标记
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //敏感词过滤
        comment.setContent(sensitiveFilter.filterSensitiveWord(comment.getContent()));
        int rows = commentMapper.insertComment(comment);

        //更新discuss_post表中的评论数量
        if (comment.getEntityType() == 1) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }
        return rows;
    }
}
