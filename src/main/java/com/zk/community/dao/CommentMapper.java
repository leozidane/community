package com.zk.community.dao;

import com.zk.community.entity.Comment;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    int selectCountByEntity(int entityType, int entityId);

    int insertComment(Comment comment);

    int selectCountByUserId(int userId, int entityType);

    List<Comment> selectCommentsByUserId(int userId, int entityType, int offset, int limit);

    Comment selectCommentById(int id);
}
