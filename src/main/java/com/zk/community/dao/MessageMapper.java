package com.zk.community.dao;

import com.zk.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户的会话列表，每个会话只返回最新的信息
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户所有的会话列表数量
    int selectConversationCount(int userId);

    //查询某个会话的所有会话列表
    List<Message> selectLetters(String conversationId, int offset, int limit);

    //查询某个会话的所有私信数量
    int selectLetterCount(String conversationId);

    //插入消息
    int insertMessage(Message message);

    //改变消息状态
    int updateStatus(List<Integer> ids, int status);

    //查询某个私信未读消息数量或当前用户所有未读私信数量
    int selectLetterUnreadCount(int userId, String conversationId);
}
