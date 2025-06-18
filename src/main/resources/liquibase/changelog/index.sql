-- liquibase formatted  sql

-- changeset dmitryDavidov:1

CREATE INDEX idx_room_users_room_id ON room_users(room_id);
CREATE INDEX idx_room_users_users_id ON room_users(users_id);
