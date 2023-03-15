CREATE DATABASE mydb;
USE mydb;
CREATE TABLE member(
    id VARCHAR(35) NOT NULL UNIQUE,
    pw VARCHAR(35) NOT NULL,
    display_name VARCHAR(35) NOT NULL,
    status_message VARCHAR(105),
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
);

CREATE TABLE personal_chat (
    id BIGINT NOT NULL AUTO_INCREMENT,
    sender_user_id VARCHAR(35) NOT NULL,
    receiver_user_id VARCHAR(35) NOT NULL,
    group_id VARCHAR(71) NOT NULL,
    content VARCHAR(5000) DEFAULT '',
    read_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT 0,
    PRIMARY KEY(id)
);