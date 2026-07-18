-- ===== users =====
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    password VARCHAR(255) NOT NULL,
    penalty_count INT NOT NULL DEFAULT 0,
    manner_warning_count INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    suspended_until DATETIME,
    notification_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
);

-- ===== courses =====
CREATE TABLE courses (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100),
    professor VARCHAR(50),
    class_time VARCHAR(100),
    course_type VARCHAR(50),
    department VARCHAR(50),
    category VARCHAR(50),
    area VARCHAR(50),
    is_graduation_req BOOLEAN,
    PRIMARY KEY (id)
);

-- ===== notifications =====
CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(30) NOT NULL,
    title VARCHAR(255),
    body VARCHAR(500),
    deep_link VARCHAR(255),
    related_id BIGINT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ===== reports =====
CREATE TABLE reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    reporter_id BIGINT NOT NULL,
    reported_user_id BIGINT NOT NULL,
    reason VARCHAR(20) NOT NULL,
    image_urls VARCHAR(1000),
    status VARCHAR(20) NOT NULL DEFAULT 'RECEIVED',
    exchange_id BIGINT,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (reported_user_id) REFERENCES users(id)
);

-- ===== user_blocks =====
CREATE TABLE user_blocks (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             blocker_id BIGINT NOT NULL,
                             blocked_id BIGINT NOT NULL,
                             created_at DATETIME NOT NULL,
                             PRIMARY KEY (id),
                             UNIQUE KEY uq_user_blocks (blocker_id, blocked_id),
                             FOREIGN KEY (blocker_id) REFERENCES users(id),
                             FOREIGN KEY (blocked_id) REFERENCES users(id)
);

-- ===== verification_logs =====
CREATE TABLE verification_logs (
                                   id BIGINT NOT NULL AUTO_INCREMENT,
                                   exchange_id BIGINT NOT NULL,
                                   user_id BIGINT NOT NULL,
                                   verify_type VARCHAR(10) NOT NULL,
                                   image_url VARCHAR(500) NOT NULL,
                                   qr_valid TINYINT(1),
                                   verified_at DATETIME,
                                   status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                   created_at DATETIME NOT NULL,
                                   PRIMARY KEY (id),
                                   FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ===== push_subscriptions =====
CREATE TABLE push_subscriptions (
                                    id BIGINT NOT NULL AUTO_INCREMENT,
                                    user_id BIGINT NOT NULL,
                                    fcm_token VARCHAR(500) NOT NULL,
                                    device_type VARCHAR(20),
                                    created_at DATETIME NOT NULL,
                                    PRIMARY KEY (id),
                                    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ===== posts =====
CREATE TABLE posts (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       discard_course_id BIGINT NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       kakao_link VARCHAR(255),
                       created_at DATETIME NOT NULL,
                       PRIMARY KEY (id),
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       FOREIGN KEY (discard_course_id) REFERENCES courses(id)
);

-- ===== post_wanted_courses =====
CREATE TABLE post_wanted_courses (
                                     id BIGINT NOT NULL AUTO_INCREMENT,
                                     post_id BIGINT NOT NULL,
                                     course_id BIGINT NOT NULL,
                                     priority INT NOT NULL,
                                     PRIMARY KEY (id),
                                     UNIQUE KEY uk_post_priority (post_id, priority),
                                     FOREIGN KEY (post_id) REFERENCES posts(id),
                                     FOREIGN KEY (course_id) REFERENCES courses(id)
);

-- ===== likes (엔티티명은 PostLike, 테이블명은 likes — LIKE가 SQL 예약어라 회피) =====
CREATE TABLE likes (
                       id BIGINT NOT NULL AUTO_INCREMENT,
                       user_id BIGINT NOT NULL,
                       post_id BIGINT NOT NULL,
                       created_at DATETIME NOT NULL,
                       PRIMARY KEY (id),
                       UNIQUE KEY uk_user_post (user_id, post_id),
                       FOREIGN KEY (user_id) REFERENCES users(id),
                       FOREIGN KEY (post_id) REFERENCES posts(id)
);

-- 라운지 게시글
CREATE TABLE IF NOT EXISTS lounge_posts (
                                            id            BIGINT       NOT NULL AUTO_INCREMENT,
                                            user_id       BIGINT       NOT NULL,
                                            course_id     BIGINT       NOT NULL,
                                            type          VARCHAR(255) NOT NULL,          -- TIP / CLOSURE
    title         VARCHAR(255) NOT NULL,
    content       TEXT         NOT NULL,
    like_count    INT          NOT NULL DEFAULT 0,
    comment_count INT          NOT NULL DEFAULT 0,
    created_at    DATETIME     NOT NULL,
    updated_at    DATETIME     NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_lounge_post_user   FOREIGN KEY (user_id)   REFERENCES users (id),
    CONSTRAINT fk_lounge_post_course FOREIGN KEY (course_id) REFERENCES courses (id),
    INDEX idx_lounge_post_course  (course_id),
    INDEX idx_lounge_post_type    (type),
    INDEX idx_lounge_post_created (created_at),
    INDEX idx_lounge_post_user    (user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 라운지 댓글
CREATE TABLE IF NOT EXISTS lounge_comments (
                                               id         BIGINT   NOT NULL AUTO_INCREMENT,
                                               post_id    BIGINT   NOT NULL,
                                               user_id    BIGINT   NOT NULL,
                                               content    TEXT     NOT NULL,
                                               created_at DATETIME NOT NULL,
                                               PRIMARY KEY (id),
    CONSTRAINT fk_lounge_comment_post FOREIGN KEY (post_id) REFERENCES lounge_posts (id),
    CONSTRAINT fk_lounge_comment_user FOREIGN KEY (user_id) REFERENCES users (id),
    INDEX idx_lounge_comment_post (post_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 라운지 좋아요
CREATE TABLE IF NOT EXISTS lounge_likes (
                                            id         BIGINT   NOT NULL AUTO_INCREMENT,
                                            post_id    BIGINT   NOT NULL,
                                            user_id    BIGINT   NOT NULL,
                                            created_at DATETIME NOT NULL,
                                            PRIMARY KEY (id),
    CONSTRAINT fk_lounge_like_post FOREIGN KEY (post_id) REFERENCES lounge_posts (id),
    CONSTRAINT fk_lounge_like_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_lounge_like_post_user UNIQUE (post_id, user_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 라운지 북마크
CREATE TABLE IF NOT EXISTS lounge_bookmarks (
                                                id         BIGINT   NOT NULL AUTO_INCREMENT,
                                                post_id    BIGINT   NOT NULL,
                                                user_id    BIGINT   NOT NULL,
                                                created_at DATETIME NOT NULL,
                                                PRIMARY KEY (id),
    CONSTRAINT fk_lounge_bookmark_post FOREIGN KEY (post_id) REFERENCES lounge_posts (id),
    CONSTRAINT fk_lounge_bookmark_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_lounge_bookmark_post_user UNIQUE (post_id, user_id),
    INDEX idx_lounge_bookmark_user_created (user_id, created_at)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
