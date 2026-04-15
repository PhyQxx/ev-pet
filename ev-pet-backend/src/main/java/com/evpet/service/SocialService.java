package com.evpet.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.evpet.mapper.*;
import com.evpet.model.*;
import com.evpet.vo.ApiResponse;
import com.evpet.vo.SocialVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocialService {

    private final FriendMapper friendMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final PetMapper petMapper;

    public SocialVO getFriends(Long userId) {
        // 获取好友列表
        List<Friend> friends = friendMapper.selectList(
            new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, userId)
                .eq(Friend::getStatus, "accepted")
        );
        
        List<SocialVO.FriendVO> friendVOs = new ArrayList<>();
        for (Friend f : friends) {
            User friend = userMapper.selectById(f.getFriendId());
            Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, f.getFriendId()));
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
        
        List<SocialVO.FriendRequestVO> requestVOs = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Friend r : requests) {
            User user = userMapper.selectById(r.getUserId());
            Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, r.getUserId()));
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
        wrapper.eq(Post::getStatus, "approved").orderByDesc(Post::getCreateTime).last("LIMIT 50");
        
        if ("hot".equals(filter)) {
            wrapper.orderByDesc(Post::getLikes);
        }
        
        List<Post> posts = postMapper.selectList(wrapper);
        List<SocialVO.PostVO> postVOs = new ArrayList<>();
        
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Post p : posts) {
            User user = userMapper.selectById(p.getUserId());
            Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, p.getUserId()));
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
        List<User> users = userMapper.selectList(null);
        List<SocialVO.RankVO> ranks = new ArrayList<>();
        
        users.sort((a, b) -> {
            if ("gold".equals(type)) {
                return b.getGold() - a.getGold();
            }
            return b.getLevel() - a.getLevel();
        });
        
        int rank = 1;
        for (User u : users) {
            Pet pet = petMapper.selectOne(new LambdaQueryWrapper<Pet>().eq(Pet::getUserId, u.getId()));
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
            if (rank > 20) break;
        }
        
        return SocialVO.builder().rankings(ranks).build();
    }

    @Transactional
    public ApiResponse<String> addFriend(Long userId, Long friendId) {
        // 检查是否已经是好友
        Friend existing = friendMapper.selectOne(
            new LambdaQueryWrapper<Friend>()
                .eq(Friend::getUserId, userId)
                .eq(Friend::getFriendId, friendId)
        );
        
        if (existing != null) {
            return ApiResponse.error("已经是好友或已发送请求");
        }
        
        Friend friend = new Friend();
        friend.setUserId(userId);
        friend.setFriendId(friendId);
        friend.setStatus("pending");
        friend.setCreateTime(LocalDateTime.now());
        friend.setUpdateTime(LocalDateTime.now());
        friendMapper.insert(friend);
        
        return ApiResponse.success("好友请求已发送");
    }

    @Transactional
    public ApiResponse<String> acceptFriend(Long userId, Long requestId) {
        Friend request = friendMapper.selectById(requestId);
        if (request == null || !request.getFriendId().equals(userId)) {
            return ApiResponse.error("请求不存在");
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
        
        return ApiResponse.success("已接受好友请求");
    }

    @Transactional
    public ApiResponse<String> rejectFriend(Long userId, Long requestId) {
        Friend request = friendMapper.selectById(requestId);
        if (request == null || !request.getFriendId().equals(userId)) {
            return ApiResponse.error("请求不存在");
        }
        
        request.setStatus("rejected");
        request.setUpdateTime(LocalDateTime.now());
        friendMapper.updateById(request);
        
        return ApiResponse.success("已拒绝好友请求");
    }

    @Transactional
    public ApiResponse<String> publishPost(Long userId, String content) {
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        post.setLikes(0);
        post.setComments(0);
        post.setStatus("approved"); // 简化处理，直接通过
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insert(post);
        
        return ApiResponse.success("发布成功");
    }

    @Transactional
    public ApiResponse<String> likePost(Long userId, Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return ApiResponse.error("动态不存在");
        }
        
        post.setLikes(post.getLikes() + 1);
        postMapper.updateById(post);
        
        return ApiResponse.success("点赞成功");
    }

    private String getPetEmoji(int stage) {
        return switch (stage) {
            case 1 -> "🐣";
            case 2 -> "🐥";
            default -> "🐦";
        };
    }
}
