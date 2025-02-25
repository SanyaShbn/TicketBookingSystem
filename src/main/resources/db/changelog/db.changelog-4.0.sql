--liquibase formatted sql

-- changeset shubinalex:1
ALTER TABLE sport_event ADD COLUMN poster_image_url VARCHAR(255);
