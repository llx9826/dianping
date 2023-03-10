package com.hmdp.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.entity.User;
import com.hmdp.holder.UserHolder;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IBlogService;
import com.hmdp.service.IFollowService;
import com.hmdp.service.IUserService;
import com.sun.istack.internal.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.hmdp.utils.RedisConstants.BLOG_LIKED_KEY;
import static com.hmdp.utils.RedisConstants.FEED_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author linxuan.li
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IFollowService followService;

    @Resource
    private IUserService userService;

    @Override
    public Result saveBlog(Blog blog) {
        // 1.获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 2.保存探店笔记
        boolean isSuccess = save(blog);
        if (!isSuccess) {
            return Result.fail("新增笔记失败!");
        }
        // 3.查询笔记作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        // 4.推送笔记id给所有粉丝
        for (Follow follow : follows) {
            // 4.1.获取粉丝id
            Long userId = follow.getUserId();
            // 4.2.推送
            String key = FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key, blog.getId().toString(), System.currentTimeMillis());
        }
        // 5.返回id
        return Result.ok(blog.getId());
    }

    @Override
    public Result likeBlog(Long id) {
        UserDTO user = UserHolder.getUser();
        String key = BLOG_LIKED_KEY + id;
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, user.getId().toString());
        if (Objects.equals(member, false)) {
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<Blog>();
            updateWrapper.setSql("liked = liked + 1").eq("id", id);
            boolean update = update(updateWrapper);
            if (update) {
                stringRedisTemplate.opsForSet().add(key, user.getId().toString());
            }
        } else {
            UpdateWrapper<Blog> updateWrapper = new UpdateWrapper<Blog>();
            updateWrapper.setSql("liked = liked - 1").eq("id", id);
            boolean update = update(updateWrapper);
            if (update) {
                stringRedisTemplate.opsForSet().remove(key, user.getId().toString());
            }
        }
        return Result.ok();
    }

    public Result queryBlogById(Long id) {
        Blog blog = getById(id);
        queryBlogUser(blog);
        isBlogLikes(blog);
        return Result.ok(blog);
    }

    private void queryBlogUser(@NotNull Blog blog) {
        User user = userService.getById(blog.getUserId());
        blog.setIcon(user.getIcon());
        blog.setName(user.getNickName());
    }

    private void isBlogLikes(@NotNull Blog blog) {
        UserDTO user = UserHolder.getUser();
        String key = BLOG_LIKED_KEY + blog.getId();
        Boolean member = stringRedisTemplate.opsForSet().isMember(key, user.getId().toString());
        blog.setIsLike(member);
    }

    public Result queryBlogLikes(Long id) {
        return Result.ok();
    }


}
