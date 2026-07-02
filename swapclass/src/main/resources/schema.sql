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