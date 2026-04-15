-- EV Pet Database Schema
CREATE DATABASE IF NOT EXISTS ev_pet DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE ev_pet;

-- 用户表
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    open_id VARCHAR(128) UNIQUE COMMENT '微信openId',
    phone VARCHAR(32) UNIQUE COMMENT '手机号',
    nickname VARCHAR(64) COMMENT '昵称',
    avatar VARCHAR(512) COMMENT '头像URL',
    gold INT DEFAULT 1000 COMMENT '金币初始1000',
    level INT DEFAULT 1 COMMENT '用户等级',
    exp INT DEFAULT 0 COMMENT '经验值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_open_id (open_id),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 宠物表
CREATE TABLE pets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '主人ID',
    name VARCHAR(64) DEFAULT '小可爱' COMMENT '宠物名',
    stage TINYINT DEFAULT 1 COMMENT '进化阶段1/2/3',
    level INT DEFAULT 1 COMMENT '等级',
    exp INT DEFAULT 0 COMMENT '经验值',
    health INT DEFAULT 100 COMMENT '体力0-100',
    fullness INT DEFAULT 100 COMMENT '饱食度0-100',
    mood INT DEFAULT 100 COMMENT '心情0-100',
    appearance JSON COMMENT '装扮外观',
    last_feed_time DATETIME COMMENT '最后喂食时间',
    last_bath_time DATETIME COMMENT '最后洗澡时间',
    last_play_time DATETIME COMMENT '最后陪玩时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 道具表
CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '道具名',
    description VARCHAR(256) COMMENT '描述',
    category VARCHAR(32) NOT NULL COMMENT '分类 food/clothing/accessory/bg',
    price INT DEFAULT 0 COMMENT '价格金币',
    rmb_price DECIMAL(10,2) DEFAULT 0 COMMENT '价格RMB',
    icon VARCHAR(512) COMMENT '图标URL',
    effect JSON COMMENT '效果配置',
    is_free TINYINT DEFAULT 1 COMMENT '是否免费',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户道具表
CREATE TABLE user_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT DEFAULT 1 COMMENT '数量',
    is_equipped TINYINT DEFAULT 0 COMMENT '是否穿戴',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_item (user_id, item_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AI聊天记录表
CREATE TABLE chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    role VARCHAR(16) NOT NULL COMMENT 'user/assistant',
    content TEXT NOT NULL COMMENT '消息内容',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_pet (user_id, pet_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 成就表
CREATE TABLE achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL COMMENT '成就名',
    description VARCHAR(256) COMMENT '描述',
    icon VARCHAR(512) COMMENT '图标URL',
    reward_gold INT DEFAULT 0 COMMENT '奖励金币',
    condition_type TINYINT NOT NULL COMMENT '条件类型',
    condition_value INT NOT NULL COMMENT '条件值',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_condition (condition_type, condition_value)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 用户成就表
CREATE TABLE user_achievements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    achievement_id BIGINT NOT NULL,
    is_claimed TINYINT DEFAULT 0 COMMENT '是否已领取',
    claim_time DATETIME COMMENT '领取时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_achievement (user_id, achievement_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 打工记录表
CREATE TABLE work_records (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    work_name VARCHAR(64) NOT NULL COMMENT '工作名称',
    duration INT COMMENT '工作时长(秒)',
    earnings INT COMMENT '收益金币',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 初始道具数据
INSERT INTO items (name, description, category, price, is_free, effect) VALUES
-- 食品类
('小面包', '恢复10点饱食度', 'food', 10, 1, '{"fullness": 10}'),
('小饼干', '恢复20点饱食度', 'food', 20, 1, '{"fullness": 20}'),
('苹果', '恢复30点饱食度', 'food', 30, 1, '{"fullness": 30}'),
('生日蛋糕', '恢复50点饱食度+5点心情', 'food', 100, 0, '{"fullness": 50, "mood": 5}'),
-- 清洁类
('湿纸巾', '恢复10点体力', 'food', 15, 1, '{"health": 10}'),
('沐浴露', '恢复20点体力', 'food', 40, 0, '{"health": 20}'),
-- 玩具类
('小皮球', '恢复15点心情', 'food', 25, 1, '{"mood": 15}'),
('毛线球', '恢复25点心情', 'food', 50, 0, '{"mood": 25}');

-- 初始成就数据
INSERT INTO achievements (name, description, icon, reward_gold, condition_type, condition_value) VALUES
('初次见面', '创建你的第一只宠物', 'egg', 100, 4, 1),
('喂养达人', '累计喂食50次', 'food', 200, 1, 50),
('清洁大师', '累计洗澡30次', 'bath', 200, 1, 30),
('玩耍专家', '累计陪玩50次', 'play', 200, 1, 50),
('Lv10', '宠物等级达到10级', 'level', 300, 3, 10),
('Lv20', '宠物等级达到20级', 'level', 500, 3, 20),
('第一次进化', '宠物完成第一次进化', 'evolution', 400, 4, 2),
('完全进化', '宠物完成第三次进化', 'evolution', 1000, 4, 3);
