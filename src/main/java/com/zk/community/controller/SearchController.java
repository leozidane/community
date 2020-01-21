package com.zk.community.controller;

import com.zk.community.entity.DiscussPost;
import com.zk.community.entity.Page;
import com.zk.community.service.ElasticsearchService;
import com.zk.community.service.LikeService;
import com.zk.community.service.UserService;
import com.zk.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    // search?keyword=xxx
    @RequestMapping(path = "search", method = RequestMethod.GET)
    public String getSearchPage(String keyword, Page page, Model model) {
        //搜索帖子
        org.springframework.data.domain.Page<DiscussPost> searchResult =
                elasticsearchService.searchDiscussPosts(keyword, page.getCurrent() - 1, page.getLimit());
        //聚合数据
        List<Map<String, Object>> discussPosts= new ArrayList<>();
        if (searchResult != null) {
            for (DiscussPost post : searchResult){
                Map<String, Object> map = new HashMap<>();
                //帖子
                map.put("post", post);
                //发帖人
                map.put("user", userService.findUserById(post.getUserId()));
                //点赞数
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);

        //分页信息
        page.setRows(searchResult == null ? 0 : (int) searchResult.getTotalElements());
        page.setPath("/search?keyword=" + keyword);

        return "site/search";
    }
}
