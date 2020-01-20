package com.zk.community.controller;

import com.zk.community.entity.Event;
import com.zk.community.entity.Page;
import com.zk.community.entity.User;
import com.zk.community.event.EventProducer;
import com.zk.community.service.FollowService;
import com.zk.community.service.UserService;
import com.zk.community.util.CommunityConstant;
import com.zk.community.util.CommunityUtil;
import com.zk.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant{

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private EventProducer eventProducer;

    //关注
    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        //触发关注事件
        Event event = new Event()
                .setTopic(TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(entityType)
                .setEntityId(entityId)
                .setEntityUserId(entityId);
        eventProducer.fireEvent(event);

        return CommunityUtil.getJSONString(0, "已关注！");
    }

    //取消关注
    @RequestMapping(path = "/unFollow", method = RequestMethod.POST)
    @ResponseBody
    public String unFollow(int entityType, int entityId) {
        User user = hostHolder.getUser();
        followService.unFollow(user.getId(), entityType, entityId);
        return CommunityUtil.getJSONString(0, "已取消关注！");
    }

    //某个用户关注列表请求
    @RequestMapping(path = "/followeeList/{userId}", method = RequestMethod.GET)
    public String getFolloweeList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        //分页信息
        page.setLimit(5);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));
        page.setPath("/followeeList/" + userId);

        //返回列表
        List<Map<String, Object>> followeeList = followService.findFolloweeList(userId, page.getOffset(), page.getLimit());
        if (followeeList != null) {
            for (Map<String, Object> followee : followeeList) {
                //当前用户是否关注了关注人
                User u = (User) followee.get("user");
                followee.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followeeList", followeeList);
        return "/site/followee";
    }
    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

    //某个用户的粉丝列表请求
    @RequestMapping(path = "/followerList/{userId}", method = RequestMethod.GET)
    public String getFollowerList(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        model.addAttribute("user", user);

        //分页信息
        page.setLimit(5);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));
        page.setPath("/followerList/" + userId);

        //返回列表
        List<Map<String, Object>> followerList = followService.findFollowerList(userId, page.getOffset(), page.getLimit());
        if (followerList != null) {
            for (Map<String, Object> follower : followerList) {
                //当前用户是否关注了粉丝
                User u = (User) follower.get("user");
                follower.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("followerList", followerList);
        return "/site/follower";
    }
}
