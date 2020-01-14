package com.zk.community.controller;

import com.zk.community.entity.Message;
import com.zk.community.entity.Page;
import com.zk.community.entity.User;
import com.zk.community.service.MessageService;
import com.zk.community.service.UserService;
import com.zk.community.util.CommunityUtil;
import com.zk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    //私信列表
    @RequestMapping(path = "/message/list", method = RequestMethod.GET)
    public String getMessageList(Model model, Page page) {
        User user = hostHolder.getUser();

        //分页信息
        page.setPath("/message/list");
        page.setLimit(5);
        page.setRows(messageService.findConversationCount(user.getId()));

        //私信列表
        List<Message> conversationList = messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        //私信Vo列表
        List<Map<String, Object>> conversationVoList = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                HashMap<String, Object> conversationVo = new HashMap<>();
                conversationVo.put("conversation", message);
                conversationVo.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                conversationVo.put("unreadCount", messageService.findLetterUnreadCount(user.getId(), message.getConversationId()));
                int targetId = message.getFromId() == user.getId() ? message.getToId() : message.getFromId();
                conversationVo.put("target", userService.findUserById(targetId));
                conversationVoList.add(conversationVo);
            }
        }
        model.addAttribute("conversations", conversationVoList);

        //查询当前用户未读消息数量
        model.addAttribute("letterUnreadCount", messageService.findLetterUnreadCount(user.getId(), null));
        return "/site/letter";
    }

    @RequestMapping(path = "/message/detail/{conversationId}", method = RequestMethod.GET)
    public String getMessageDetail(@PathVariable("conversationId") String conversationId, Model model, Page page) {

        //分页设置
        page.setPath("/message/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setLimit(5);

        //私信详情列表
        List<Message> letterList = messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        //私信详情Vo列表
        List<Map<String, Object>> letterVoList = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                HashMap<String, Object> letterVo = new HashMap<>();
                letterVo.put("letter", message);
                letterVo.put("fromUser", userService.findUserById(message.getFromId()));
                letterVoList.add(letterVo);
            }

            //私信人
            if (letterList.size() > 0) {
                Message message = letterList.get(0);
                int targetId = message.getFromId() == hostHolder.getUser().getId() ? message.getToId() : message.getFromId();
                model.addAttribute("target", userService.findUserById(targetId));

            }
        }
        model.addAttribute("letters", letterVoList);

        //设置为已读
        List<Integer> ids = getLetterId(letterList);
        if (ids != null) {
            messageService.readMessage(ids);
        }

        return "/site/letter-detail";

    }

    private List<Integer> getLetterId(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (message.getToId() == hostHolder.getUser().getId()) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }
    @RequestMapping(path = "/message/send", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toName, String content) {
        User target = userService.findUserByUsername(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(403, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setContent(content);
        message.setStatus(0);
        message.setCreateTime(new Date());
        if (message.getFromId() < message.getToId()) {
            message.setConversationId(message.getFromId() + "_" + message.getToId());
        }else {
            message.setConversationId(message.getToId() + "_" + message.getFromId());
        }

        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }
}
