package com.evpet.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SocialVO {
    private List<FriendVO> friends;
    private List<FriendRequestVO> friendRequests;
    private List<PostVO> posts;
    private List<RankVO> rankings;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendVO {
        private Long id;
        private Long friendId;
        private String nickname;
        private String avatar;
        private String petName;
        private Integer petStage;
        private String petEmoji;
        private Integer level;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FriendRequestVO {
        private Long id;
        private Long userId;
        private String nickname;
        private String avatar;
        private String petEmoji;
        private String createTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostVO {
        private Long id;
        private Long userId;
        private String nickname;
        private String avatar;
        private String petEmoji;
        private String content;
        private String image;
        private Integer likes;
        private Integer comments;
        private Boolean liked;
        private String createTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankVO {
        private Integer rank;
        private Long userId;
        private String nickname;
        private String petEmoji;
        private Integer level;
        private Integer petStage;
        private String value;
    }
}
