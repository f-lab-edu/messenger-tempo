DELIMITER $$

DROP TRIGGER IF EXISTS `BACKUP_DELETED_PERSONAL_CHAT`;
CREATE TRIGGER `BACKUP_DELETED_PERSONAL_CHAT`
    BEFORE DELETE ON personal_chat
    FOR EACH ROW
BEGIN
    INSERT INTO personal_chat_backup(id, sender_user_id, receiver_user_id, group_id, content, read_at, created_at)
        VALUE (OLD.id, OLD.sender_user_id, OLD.receiver_user_id, OLD.group_id, OLD.content, OLD.read_at, OLD.created_at);
END $$

DELIMITER ;