-- liquibase formatted sql

-- changeset atikhonov:1
CREATE TABLE notification_tasks (
                       id SERIAL,
                       chat_id BIGINT,
                       notification TEXT,
                       time_to_send TIMESTAMP
);