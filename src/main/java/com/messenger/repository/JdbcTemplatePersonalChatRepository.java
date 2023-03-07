package com.messenger.repository;

import com.messenger.domain.Chat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@Slf4j
public class JdbcTemplatePersonalChatRepository implements PersonalChatRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplatePersonalChatRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<Chat> chatRowMapper() {
        return (rs, rowNum) -> Chat.builder()
                .id(rs.getLong("id"))
                .senderUserId(rs.getString("sender_user_id"))
                .receiverUserId(rs.getString("receiver_user_id"))
                .content(rs.getString("content"))
                .unread_count(rs.getShort("unread_count"))
                .created_at(rs.getTimestamp("created_at"))
                .deleted(rs.getBoolean("deleted"))
                .build();
    }

    /**
     * 1:1 메시지를 저장소에 저장
     * @param chat 저장할 메시지 객체
     * @return 저장한 메시지 객체
     */
    @Override
    public Chat save(Chat chat) {
        String sql = "INSERT INTO personal_chat(sender_user_id, receiver_user_id, content) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("chat={}", chat);
        try {
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id"});
                ps.setString(1, chat.getSenderUserId());
                ps.setString(2, chat.getReceiverUserId());
                ps.setString(3, chat.getContent());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id).orElseThrow(() -> new NullPointerException("cannot find chat by id"));
    }

    /**
     * 메시지 id 기반으로 메시지 하나를 삭제
     * (실제로는 deleted 칼럼을 1로 설정하여 비표시 처리)
     * @param chatId 메시지 id
     * @param userId    사용자 id
     * @return (Nullable) 삭제한 메시지 객체
     */
    @Override
    public Chat deleteOne(long chatId, String userId) {
        // 전송 사용자 id가 일치해야만 삭제 처리
        String sql = "UPDATE personal_chat SET deleted = 1 WHERE id = ? AND sender_user_id = ?";
        Object[] args = {chatId, userId};
        log.debug("delete chat chatId={}, userId={}", chatId, userId);
        int update = jdbcTemplate.update(sql, args);
        if (update == 0) {
            throw new NullPointerException("cannot delete chat");
        }
        return findById(chatId).orElseThrow(() -> new NullPointerException("cannot find chat by id"));
    }

    /**
     * 메시지 id 기반으로 메시지를 검색
     * @param chatId 검색할 메시지 id
     * @return (Nullable) 메시지 객체
     */
    @Override
    public Optional<Chat> findById(long chatId) {
        String sql = "SELECT * FROM personal_chat WHERE id = ?";
        List<Chat> result = jdbcTemplate.query(sql, chatRowMapper(), chatId);
        return result.stream().findAny();
    }

    /**
     * (개발자용) 모든 메시지를 리스트로 반환
     * 삭제된 메시지도 모두 포함된다
     * @return 메시지 객체 리스트
     */
    @Override
    public List<Chat> findAll() {
        String sql = "SELECT * FROM personal_chat";
        return jdbcTemplate.query(sql, chatRowMapper());
    }


    /**
     * 전송 사용자 id 기반으로 삭제되지 않은 메시지를 검색
     * @param senderUserId 메시지 전송 사용자 id
     * @return 메시지 객체 리스트
     */
    @Override
    public List<Chat> findBySender(String senderUserId) {
        String sql = "SELECT * FROM personal_chat WHERE sender_user_id = ? AND deleted = 0";
        return jdbcTemplate.query(sql, chatRowMapper(), senderUserId);
    }

    /**
     * 수신 사용자 id 기반으로 삭제되지 않은 메시지를 검색
     * @param receiverUserId  메시지 수신 사용자 id
     * @return 메시지 객체 리스트
     */
    @Override
    public List<Chat> findByReceiver(String receiverUserId) {
        String sql = "SELECT * FROM personal_chat WHERE receiver_user_id = ? AND deleted = 0";
        return jdbcTemplate.query(sql, chatRowMapper(), receiverUserId );
    }

}
