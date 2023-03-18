CREATE INDEX idx_group_id ON personal_chat (group_id, id);
CREATE INDEX idx_receiver_id ON personal_chat (receiver_user_id, id);
CREATE INDEX idx_sender_id ON personal_chat (sender_user_id, id);
CREATE INDEX idx_receiver_sender_id ON personal_chat (receiver_user_id, sender_user_id, id);