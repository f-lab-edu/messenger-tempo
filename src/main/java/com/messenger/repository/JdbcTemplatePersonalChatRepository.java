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
                .message_from(rs.getString("message_from"))
                .message_to(rs.getString("message_to"))
                .message(rs.getString("message"))
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
        String sql = "INSERT INTO personal_chat(message_from, message_to, message) values(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("chat={}", chat);
        try {
            jdbcTemplate.update(conn -> {
                PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id"});
                ps.setString(1, chat.getMessage_from());
                ps.setString(2, chat.getMessage_to());
                ps.setString(3, chat.getMessage());
                return ps;
            }, keyHolder);
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
//        List<Map<String, Object>> keyList = keyHolder.getKeyList();
//        for (Map<String, Object> t : keyList) {
//            for (String t2 : t.keySet()) {
//                log.debug("key={}, value={}", t2, t.get(t2));
//            }
//            log.debug("============");
//        }
        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id).orElseThrow(() -> new NullPointerException("cannot find chat by id"));
    }

    /**
     * 메시지 id 기반으로 메시지 하나를 삭제
     * (실제로는 deleted 칼럼을 1로 설정하여 비표시 처리)
     * @param messageId 메시지 id
     * @param userId    사용자 id
     * @return (Nullable) 삭제한 메시지 객체
     */
    @Override
    public Optional<Chat> deleteOne(long messageId, String userId) {
        // 전송 사용자 id가 일치해야만 삭제 처리
        String sql = "UPDATE personal_chat SET deleted = 1 WHERE id = ? AND message_from = ?";
        Object[] args = {messageId, userId};
        log.debug("delete chat messageId={}, userId={}", messageId, userId);
        int update = 0;
        try {
            update = jdbcTemplate.update(sql, args);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }
        if (update == 0) {
            return Optional.empty();
        }
        return findById(messageId);
    }

    /**
     * 메시지 id 기반으로 메시지를 검색
     * @param id 검색할 메시지 id
     * @return (Nullable) 메시지 객체
     */
    @Override
    public Optional<Chat> findById(long id) {
        String sql = "SELECT * FROM personal_chat WHERE id = ?";
        List<Chat> result = jdbcTemplate.query(sql, chatRowMapper(), id);
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
     * @param message_from 메시지 전송 사용자 id
     * @return 메시지 객체 리스트
     */
    @Override
    public List<Chat> findByFrom(String message_from) {
        String sql = "SELECT * FROM personal_chat WHERE message_from = ? AND deleted = 0";
        return jdbcTemplate.query(sql, chatRowMapper(), message_from);
    }

    /**
     * 수신 사용자 id 기반으로 삭제되지 않은 메시지를 검색
     * @param message_to 메시지 수신 사용자 id
     * @return 메시지 객체 리스트
     */
    @Override
    public List<Chat> findByTo(String message_to) {
        String sql = "SELECT * FROM personal_chat WHERE message_to = ? AND deleted = 0";
        return jdbcTemplate.query(sql, chatRowMapper(), message_to);
    }

}
