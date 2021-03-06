package com.zk.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.zk.community.annotation.LoginRequired;
import com.zk.community.entity.Comment;
import com.zk.community.entity.DiscussPost;
import com.zk.community.entity.Page;
import com.zk.community.entity.User;
import com.zk.community.service.*;
import com.zk.community.util.CommunityConstant;
import com.zk.community.util.CommunityUtil;
import com.zk.community.util.HostHolder;
import com.zk.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private CommentService commentService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String upload;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.sercet}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;


    //用户设置
    @LoginRequired
    @RequestMapping(path = "/setting", method = RequestMethod.GET)
    public String getSettingPage(Model model) {
        //上传文件名称
        String fileName = CommunityUtil.generateUUID();
        //设置响应信息
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        //生成上传凭证
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);

        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);
        return "/site/setting";
    }

    //更新头像路径
    @RequestMapping(path = "/header/url", method = RequestMethod.POST)
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "文件名不能为空");
        }
        String headerUrl = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), headerUrl);
        return CommunityUtil.getJSONString(0);
    }


    //废弃
    //头像上传
    @LoginRequired
    @RequestMapping(path = "/upload", method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "您还没有选择图片");
            return "/site/setting";
        }

        //判断上传文件格式
        String filename = headerImage.getOriginalFilename();
        String suffix = filename.substring(filename.lastIndexOf("."));
        if (StringUtils.isBlank(suffix) || !(suffix.equals(".png") || suffix.equals(".jpg") || suffix.equals(".jpeg"))) {
            model.addAttribute("error", "文件的格式不正确");
            return "/site/setting";
        }

        //生成随机文件名
        filename = CommunityUtil.generateUUID() + suffix;
        //确定文件存放路径
        File file = new File(upload);
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            //存储文件到服务器本地
            headerImage.transferTo(new File(file, filename));
        } catch (IOException e) {
           logger.error("上传文件失败" + e.getMessage());
           throw new RuntimeException("上传文件失败，服务器发生异常", e);
        }

        //更新当前用户的头像路径(web的访问路径)
        //http:localhost/community/user/header/xxx.png
        String headerUrl = domain + contextPath + "/user/header/" + filename;
        User user = hostHolder.getUser();
        userService.updateHeader(user.getId(), headerUrl);

        return "redirect:/index";
    }

    //废弃
    //显示用户头像
    @RequestMapping(path = "/header/{filename}", method = RequestMethod.GET)
    public void getHeader(@PathVariable("filename") String filename, HttpServletResponse response) {
        //服务器存放的路径
        filename = upload + "/" + filename;
        //文件后缀
        String suffix = filename.substring(filename.lastIndexOf("."));
        //响应图片
        response.setContentType("image/" + suffix);

        try ( FileInputStream fis = new FileInputStream(filename);
              ServletOutputStream os = response.getOutputStream();
        ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            logger.error("读取头像失败" + e.getMessage());
        }
    }

    //更改密码
    @LoginRequired
    @RequestMapping(path = "/updatePassword", method = RequestMethod.POST)
    public String updatePassword(String oldPassword, String newPassword, String confirmPassword, Model model) {
        //空值判断
        if (StringUtils.isBlank(oldPassword)) {
            model.addAttribute("oldError", "请输入原始密码！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(newPassword)) {
            model.addAttribute("newError", "请输入新密码！");
            return "/site/setting";
        }
        if (StringUtils.isBlank(confirmPassword)) {
            model.addAttribute("confirmError", "请确认新密码！");
            return "/site/setting";
        }

        //相等判断
        if (oldPassword.equals(newPassword)) {
            model.addAttribute("newError", "两次密码不能相同，请重新输入");
            return "/site/setting";
        }
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("confirmError", "两次密码不一致，请重新输入");
            return "/site/setting";
        }
        User user = hostHolder.getUser();
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        if (!oldPassword.equals(user.getPassword())) {
            model.addAttribute("oldError", "原始密码不正确，请重新输入！");
            return "/site/setting";
        }

        //更新密码
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        userService.updatePassword(user.getId(), newPassword);

        return "redirect:/index";
    }

    //个人主页：信息
    @RequestMapping(path = "/profile/{userId}", method = RequestMethod.GET)
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        //用户
        model.addAttribute("user", user);
        //点赞数
        long likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);
        //关注人数
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);
        //粉丝数量
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);
        //当前用户的关注状态
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed = followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "/site/profile";
    }
    //个人主页：回复
    @RequestMapping(path = "/post/{userId}", method = RequestMethod.GET)
    public String getMyPostPage(@PathVariable("userId") int userId, Model model, Page page) {
        //设置分页信息
        page.setLimit(10);
        page.setRows(discussPostService.findDiscussPostRow(userId));
        page.setPath("/user/post/" + userId);

        //得到帖子列表
        List<DiscussPost> posts = discussPostService.findDiscussPosts(userId, page.getOffset(), page.getLimit(), 0);
        List<Map<String, Object>> postVoList = new ArrayList<>();
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> postVo = new HashMap<>();
                postVo.put("post", post);
                long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                postVo.put("likeCount", likeCount);
                postVoList.add(postVo);
            }
        }
        model.addAttribute("posts", postVoList);
        return "/site/my-post";
    }
    //个人主页：评论
    @RequestMapping(path = "/comment/{userId}", method = RequestMethod.GET)
    public String getCommentPage(@PathVariable("userId") int userId, Page page, Model model) {
        //设置分页信息
        page.setPath("/user/comment/" + userId);
        page.setRows(commentService.findCommentCountByUserId(userId, ENTITY_TYPE_POST));

        //得到用户对帖子的回复
        List<Comment> commentList = commentService.findCommentsByUserId(userId, ENTITY_TYPE_POST, page.getOffset(), page.getLimit());
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment", comment);
                DiscussPost post = discussPostService.findDiscussPostById(comment.getEntityId());
                if (post != null) {
                    commentVo.put("post", post);
                    commentVoList.add(commentVo);
                }
            }
        }
        model.addAttribute("replyList", commentVoList);
        return "/site/my-reply";
    }
}
