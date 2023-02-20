CREATE DATABASE mydb;
USE mydb;
create table member(
    id VARCHAR(35) NOT NULL UNIQUE,
    pw VARCHAR(35) NOT NULL,
    display_name VARCHAR(35) NOT NULL,
    status_message VARCHAR(105),
    join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY(id)
);