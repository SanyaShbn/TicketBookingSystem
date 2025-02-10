--liquibase formatted sql

--changeset shubinalex:1
CREATE TABLE IF NOT EXISTS changelog_test
(
    test_id SERIAL PRIMARY KEY,
    test_column INT
);