-- 用户表索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_open_id ON users(openId);
CREATE UNIQUE INDEX IF NOT EXISTS idx_users_phone ON users(phone);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_update_time ON users(updateTime);

-- 宠物表索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_pets_user_id ON pets(userId);

-- 打工记录索引
CREATE INDEX IF NOT EXISTS idx_work_records_user_id ON work_records(userId);
CREATE INDEX IF NOT EXISTS idx_work_records_status ON work_records(status);

-- 帖子索引
CREATE INDEX IF NOT EXISTS idx_posts_user_id ON posts(userId);
CREATE INDEX IF NOT EXISTS idx_posts_status ON posts(status);
CREATE INDEX IF NOT EXISTS idx_posts_create_time ON posts(createTime);

-- 好友索引
CREATE INDEX IF NOT EXISTS idx_friends_user_id ON friends(userId);
CREATE INDEX IF NOT EXISTS idx_friends_friend_id ON friends(friendId);
CREATE UNIQUE INDEX IF NOT EXISTS idx_friends_user_friend ON friends(userId, friendId);

-- 聊天消息索引
CREATE INDEX IF NOT EXISTS idx_chat_messages_user_id ON chat_messages(userId);
CREATE INDEX IF NOT EXISTS idx_chat_messages_pet_id ON chat_messages(petId);

-- 成就索引
CREATE INDEX IF NOT EXISTS idx_user_achievements_user_id ON user_achievements(userId);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_achievements_user_achievement ON user_achievements(userId, achievementId);

-- 道具索引
CREATE INDEX IF NOT EXISTS idx_user_items_user_id ON user_items(userId);

-- 内容审核索引
CREATE INDEX IF NOT EXISTS idx_content_reviews_status ON content_reviews(status);

-- 公告索引
CREATE INDEX IF NOT EXISTS idx_announcements_status ON announcements(status);

-- 活动索引
CREATE INDEX IF NOT EXISTS idx_activities_status ON activities(status);
