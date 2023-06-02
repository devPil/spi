# User Storage SPI 테스트


```mariadb
/* Example DATABASE Script (mariadb) */

CREATE DATABASE TEST;

USE TEST;

# drop table users;

CREATE TABLE `users`
(
    `username`  varchar(64)  NOT NULL,
    `password`  varchar(64)  NOT NULL,
    `email`     varchar(128) DEFAULT NULL,
    `firstName` varchar(128) NOT NULL,
    `lastName`  varchar(128) NOT NULL,
    `birthDate` date         NOT NULL,
    PRIMARY KEY (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = latin1
  COLLATE = latin1_swedish_ci
;

INSERT INTO TEST.users (username, password, email, firstName, lastName, birthDate) VALUES ('user1', '1', '1@example.com', 'first1', 'last1', '2023-05-25');
INSERT INTO TEST.users (username, password, email, firstName, lastName, birthDate) VALUES ('user2', '1', '2@example.com', 'first2', 'last2', '2023-05-25');
INSERT INTO TEST.users (username, password, email, firstName, lastName, birthDate) VALUES ('user3', '1', '3@example.com', 'first3', 'last3', '2023-05-25');

```