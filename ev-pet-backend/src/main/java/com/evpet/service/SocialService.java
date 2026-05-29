package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.evpet.mapper.*;
import com.evpet.model.*;
import com.evpet.vo.SocialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final FriendMapper friendMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PetMapper petMapper;
    private final CommentMapper commentMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public SocialVO getFriends(Long userId) {
        // 获取好友列表
        List<Friend> friends = friendMapper.selectList(
            new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, userId)
                .eq(Friend::getStatus, "accepted")
        );

        // 批量查询好友的用户信息和宠物信息
        List<Long> friendIds = friends.stream().map(Friend::getFriendId).collect(Collectors.toList());
        Map<Long, User> friendUserMap = friendIds.isEmpty()
            ? Map.of()
            : userMapper.selectBatchIds(friendIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Pet> friendPetMap = friendIds.isEmpty()
            ? Map.of()
            : petMapper.selectList(new LambdaQueryWrapper<Pet>().in(Pet::getUserId, friendIds))
                .stream().collect(Collectors.toMap(Pet::getUserId, Function.identity()));

        List<SocialVO.FriendVO> friendVOs = new ArrayList<>();
        for (Friend f : friends) {
            User friend = friendUserMap.get(f.getFriendId());
            Pet pet = friendPetMap.get(f.getFriendId());
            if (friend != null) {
                friendVOs.add(SocialVO.FriendVO.builder()
                    .id(f.getId())
                    .friendId(friend.getId())
                    .nickname(friend.getNickname())
                    .avatar(friend.getAvatar())
                    .petName(pet != null ? pet.getName() : "")
                    .petStage(pet != null ? pet.getStage() : 1)
                    .petEmoji(getPetEmoji(pet != null ? pet.getStage() : 1))
                    .level(friend.getLevel())
                    .build());
            }
        }

        // 获取好友请求
        List<Friend> requests = friendMapper.selectList(
            new LambdaQueryWrapper<Friend>()
                .eq(Friend::getFriendId, userId)
                .eq(Friend::getStatus, "pending")
        );

        // 批量查询请求用户的用户信息和宠物信息
        List<Long> requesterIds = requests.stream().map(Friend::getUserId).collect(Collectors.toList());
        Map<Long, User> requesterUserMap = requesterIds.isEmpty()
            ? Map.of()
            : userMapper.selectBatchIds(requesterIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Pet> requesterPetMap = requesterIds.isEmpty()
            ? Map.of()
            : petMapper.selectList(new LambdaQueryWrapper<Pet>().in(Pet::getUserId, requesterIds))
                .stream().collect(Collectors.toMap(Pet::getUserId, Function.identity()));

        List<SocialVO.FriendRequestVO> requestVOs = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Friend r : requests) {
            User user = requesterUserMap.get(r.getUserId());
            Pet pet = requesterPetMap.get(r.getUserId());
            if (user != null) {
                requestVOs.add(SocialVO.FriendRequestVO.builder()
                    .id(r.getId())
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .petEmoji(getPetEmoji(pet != null ? pet.getStage() : 1))
                    .createTime(r.getCreateTime().format(fmt))
                    .build());
            }
        }

        return SocialVO.builder()
            .friends(friendVOs)
            .friendRequests(requestVOs)
            .build();
    }

    public SocialVO getPosts(Long userId, String filter) {
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, "approved").orderByDesc(Post::getCreateTime);

        if ("hot".equals(filter)) {
            wrapper.orderByDesc(Post::getLikes);
        }

        List<Post> posts = postMapper.selectPage(new Page<>(1, 50), wrapper).getRecords();

        // 批量查询发帖用户的用户信息和宠物信息
        List<Long> postUserIds = posts.stream().map(Post::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = postUserIds.isEmpty()
            ? Map.of()
            : userMapper.selectBatchIds(postUserIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));
        Map<Long, Pet> petMap = postUserIds.isEmpty()
            ? Map.of()
            : petMapper.selectList(new LambdaQueryWrapper<Pet>().in(Pet::getUserId, postUserIds))
                .stream().collect(Collectors.toMap(Pet::getUserId, Function.identity()));

        List<SocialVO.PostVO> postVOs = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Post p : posts) {
            User user = userMap.get(p.getUserId());
            Pet pet = petMap.get(p.getUserId());
            if (user != null) {
                postVOs.add(SocialVO.PostVO.builder()
                    .id(p.getId())
                    .userId(user.getId())
                    .nickname(user.getNickname())
                    .avatar(user.getAvatar())
                    .petEmoji(getPetEmoji(pet != null ? pet.getStage() : 1))
                    .content(p.getContent())
                    .image(p.getImage())
                    .likes(p.getLikes())
                    .comments(p.getComments())
                    .liked(false)
                    .createTime(p.getCreateTime().format(fmt))
                    .build());
            }
        }

        return SocialVO.builder().posts(postVOs).build();
    }

    public SocialVO getRankings(String type) {
        // 使用 Page 在数据库层排序并只取前20条
        Page<User> page = new Page<>(1, 20);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if ("gold".equals(type)) {
            wrapper.orderByDesc(User::getGold);
        } else {
            wrapper.orderByDesc(User::getLevel).orderByDesc(User::getExp);
        }
        List<User> users = userMapper.selectPage(page, wrapper).getRecords();

        // 批量查询宠物信息
        List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        Map<Long, Pet> petMap = userIds.isEmpty()
            ? Map.of()
            : petMapper.selectList(new LambdaQueryWrapper<Pet>().in(Pet::getUserId, userIds))
                .stream().collect(Collectors.toMap(Pet::getUserId, Function.identity()));

        List<SocialVO.RankVO> ranks = new ArrayList<>();
        int rank = 1;
        for (User u : users) {
            Pet pet = petMap.get(u.getId());
            String value = "gold".equals(type) ? u.getGold() + "金币" : "Lv." + u.getLevel();
            ranks.add(SocialVO.RankVO.builder()
                .rank(rank++)
                .userId(u.getId())
                .nickname(u.getNickname())
                .petEmoji(getPetEmoji(pet != null ? pet.getStage() : 1))
                .level(u.getLevel())
                .petStage(pet != null ? pet.getStage() : 1)
                .value(value)
                .build());
        }

        return SocialVO.builder().rankings(ranks).build();
    }

    @Transactional
    public String addFriend(Long userId, Long friendId) {
        // Check for self-add
        if (userId.equals(friendId)) {
            throw new IllegalArgumentException("不能添加自己为好友");
        }

        // 检查是否已经是好友
        Friend existing = friendMapper.selectOne(
            new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId)
        );

        if (existing != null) {
            throw new IllegalStateException("已经是好友或已发送请求");
        }

        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus("pending");
        friend.setCreateTime(LocalDateTime.now());
        friend.setUpdateTime(LocalDateTime.now());
        friendMapper.insert(friend);

        return "好友请求已发送";
    }

    @Transactional
    public String acceptFriend(Long userId, Long requestId) {
        Friend request = friendMapper.selectById(requestId);
        if (request == null || !request.getFriendId().equals(userId)) {
            throw new IllegalArgumentException("请求不存在");
        }

        request.setStatus("accepted");
        request.setUpdateTime(LocalDateTime.now());
        friendMapper.updateById(request);

        // 双向添加好友
        Friend reverse = new Friend();
        reverse.setUserId(userId);
        reverse.setFriendId(request.getUserId());
        reverse.setStatus("accepted");
        reverse.setCreateTime(LocalDateTime.now());
        reverse.setUpdateTime(LocalDateTime.now());
        friendMapper.insert(reverse);

        return "已接受好友请求";
    }

    @Transactional
    public String rejectFriend(Long userId, Long requestId) {
        Friend request = friendMapper.selectById(requestId);
        if (request == null || !request.getFriendId().equals(userId)) {
            throw new IllegalArgumentException("请求不存在");
        }

        request.setStatus("rejected");
        request.setUpdateTime(LocalDateTime.now());
        friendMapper.updateById(request);

        return "已拒绝好友请求";
    }

    @Transactional
    public String publishPost(Long userId, String content) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setLikes(0);
        post.setComments(0);
        post.setStatus("approved"); // 简化处理，直接通过
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insert(post);

        return "发布成功";
    }

    @Transactional
    public String likePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new IllegalArgumentException("动态不存在");
        }

        String likeKey = "post:like:" + postId + ":" + userId;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(likeKey))) {
            throw new IllegalStateException("已经点赞过了");
        }

        stringRedisTemplate.opsForValue().set(likeKey, "1");
        post.setLikes(post.getLikes() + 1);
        postMapper.updateById(post);

        return "点赞成功";
    }

    @Transactional
    public String addComment(Long userId, Long postId, String content) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new IllegalArgumentException("动态不存在");
        }

        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreateTime(LocalDateTime.now());
        commentMapper.insert(comment);

        post.setComments(post.getComments() + 1);
        postMapper.updateById(post);

        return "评论成功";
    }

    public List<Map<String, Object>> getComments(Long postId) {
        List<Comment> comments = commentMapper.selectList(
            new LambdaQueryWrapper<Comment>()
                .eq(Comment::getPostId, postId)
                .orderByDesc(Comment::getCreateTime)
        );

        List<Long> userIds = comments.stream().map(Comment::getUserId).distinct().collect(Collectors.toList());
        Map<Long, User> userMap = userIds.isEmpty()
            ? Map.of()
            : userMapper.selectBatchIds(userIds).stream().collect(Collectors.toMap(User::getId, Function.identity()));

        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Comment c : comments) {
            User user = userMap.get(c.getUserId());
            Map<String, Object> m = new HashMap<>();
            m.put("id", c.getId());
            m.put("userId", c.getUserId());
            m.put("nickname", user != null ? user.getNickname() : "匿名");
            m.put("avatar", user != null ? user.getAvatar() : null);
            m.put("content", c.getContent());
            m.put("createTime", c.getCreateTime() != null ? c.getCreateTime().format(fmt) : "");
            result.add(m);
        }
        return result;
    }

    private String getPetEmoji(int stage) {
        return switch (stage) {
            case 1 -> "🐣";
            case 2 -> "🐥";
            default -> "🐦";
        };
    }
}
