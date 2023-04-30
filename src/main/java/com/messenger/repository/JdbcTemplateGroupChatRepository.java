package com.messenger.repository;

import com.messenger.domain.GroupChat;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.*;

@Repository
@Slf4j
public class JdbcTemplateGroupChatRepository implements GroupChatRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateGroupChatRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 1:1 메시지를 저장소에 저장
     * @param chat 저장할 메시지 객체
     * @return 저장한 메시지 객체
     */
    @Override
    public GroupChat save(GroupChat chat) {
        String sql = "INSERT INTO group_chat(sender_user_id, room_id, content) VALUES(?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        log.debug("chat={}", chat);
        jdbcTemplate.update(conn -> {
            PreparedStatement ps = conn.prepareStatement(sql, new String[] {"id"});
            ps.setString(1, chat.getSenderUserId());
            ps.setLong(2, chat.getRoomId());
            ps.setString(3, chat.getContent());
            return ps;
        }, keyHolder);

        long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return findById(id).orElseThrow(() -> new MyException(ErrorCode.NOT_FOUND_CHAT));
    }

    /**
     * 메시지 id 기반으로 메시지 하나를 삭제
     * @param chatId 메시지 id
     * @param userId 사용자 id
     */
    @Override
    public void deleteOne(long chatId, String userId) {
        // 전송 사용자 id가 일치해야만 삭제 처리
        String sql = "DELETE FROM group_chat WHERE id = ? AND sender_user_id = ?";
        Object[] args = {chatId, userId};
        log.debug("delete chat chatId={}, userId={}", chatId, userId);
        int update = jdbcTemplate.update(sql, args);
        log.debug("update={}", update);
        if (update == 0) {
            throw new MyException(ErrorCode.FAIL_DELETE_CHAT);
        }
    }

    /**
     * 메시지 id 기반으로 메시지를 검색
     * @param chatId 검색할 메시지 id
     * @return (Nullable) 메시지 객체
     */
    @Override
    public Optional<GroupChat> findById(long chatId) {
        String sql = "SELECT * FROM group_chat WHERE id = ?";
        List<GroupChat> result = jdbcTemplate.query(sql, chatRowMapper(), chatId);
        return result.stream().findAny();
    }


    /**
     * 수신 사용자 id 기반으로 삭제되지 않은 메시지를 검색
     * (커서 기반 페이지네이션)
     * @param receiverUserId  메시지 수신 사용자 id
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @Override
    public List<GroupChat> findByReceiver(String receiverUserId, Integer prevId, Integer size) {
        if (prevId == null) {
            String sql = "SELECT * FROM group_chat WHERE room_id IN (SELECT room_id FROM group_room_members WHERE user_id = ?) AND id >= 0 ORDER BY id DESC LIMIT ?";
            return jdbcTemplate.query(sql, chatRowMapper(), receiverUserId, size);
        }
        String sql = "SELECT * FROM group_chat WHERE room_id IN (SELECT room_id FROM group_room_members WHERE user_id = ?) AND id < ? ORDER BY id DESC LIMIT ?";
        return jdbcTemplate.query(sql, chatRowMapper(), receiverUserId, prevId, size);
    }


    /**
     * 자신과 상대방 사용자 id 기반으로 1:1 그룹의 메시지를 검색
     * @param userId 자신의 사용자 id
     * @param roomId 그룹 채팅방 id
     * @param prevId 이전 조회한 마지막 메시지 id
     * @param size 조회할 메시지 개수
     * @return 메시지 객체 리스트
     */
    @Override
    public List<GroupChat> findByGroup(String userId, long roomId, Integer prevId, Integer size) {
        if (prevId == null) {
            String sql = "SELECT * FROM group_chat WHERE room_id = ? AND id >= 0 ORDER BY id DESC LIMIT ?";
            return jdbcTemplate.query(sql, chatRowMapper(), roomId, size);
        }
        String sql = "SELECT * FROM group_chat WHERE room_id = ? AND id < ? ORDER BY id DESC LIMIT ?";
        return jdbcTemplate.query(sql, chatRowMapper(), roomId, prevId, size);
    }

    /**
     * 자신과 상대방 사용자 id 기반으로 1:1 그룹에서 자신이 받은 마지막 메시지를 검색
     * @param userId 자신의 사용자 id
     * @param roomId 그룹 채팅방 id
     * @return (Nullable) 메시지 객체
     */
    @Override
    public Optional<GroupChat> findLastReceivedByGroup(String userId, long roomId) {
        String sqlSelect = "SELECT * FROM group_chat WHERE sender_user_id <> ? AND room_id = ? AND id >= 0 ORDER BY id DESC LIMIT 1";
        List<GroupChat> result = jdbcTemplate.query(sqlSelect, chatRowMapper(), userId, roomId);
        return result.stream().findAny();
    }

    @Override
    public Map<String, Timestamp> getReadStatusById(long chatId) {
        String sqlSelect = "SELECT * FROM group_chat_read_time WHERE chat_id = ?";
        List<Pair<String, Timestamp>> list = jdbcTemplate.query(sqlSelect, readStatusRowMapper(), chatId);
        Map<String, Timestamp> result = new HashMap<>();
        for (Pair<String, Timestamp> t : list) {
            result.put(t.getFirst(), t.getSecond());
        }
        return result;
    }

    /**
     * 메시지를 읽음 표시
     * Chat 테이블의 read_at 칼럼을 현재 시간으로 업데이트함으로써 읽음 표시
     * @param chatId 읽음 표시할 메시지 id
     * @return (Nullable) 메시지 객체
     */
    @Override
    public Optional<GroupChat> markReadById(long chatId, String userId) {
        log.debug("mark as read by id, chatId = {}", chatId);
        String sqlInsert = "INSERT INTO group_chat_read_time(chat_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlInsert, chatId, userId);
        } catch (DuplicateKeyException e) {
            log.error("cannot insert read status: {}", e.getMessage());
        }
        return findById(chatId);
    }

    @Override
    public List<Pair<Long, Long>> listGroupByUser(String userId) {
        // TODO: 성능 개선 필요
        String sqlSelect = "SELECT room_id, max(id) max_id FROM group_chat WHERE room_id IN (SELECT room_id FROM group_room_members WHERE user_id = ?) GROUP BY room_id ORDER BY max_id DESC";
        return jdbcTemplate.query(sqlSelect, groupLastMessageRowMapper(), userId);
    }

    @Override
    public boolean belongToRoom(long roomId, String userId) {
        String sql = "SELECT * FROM group_room_members WHERE room_id = ?";
        List<String> list = jdbcTemplate.query(sql, groupMemberRowMapper(), roomId);
        return list.stream().anyMatch(someUserId -> someUserId.equals(userId));
    }

    @Override
    public List<String> makeNewGroup(List<String> memberList) {

        List<String> resultList = new ArrayList<>();

        String sqlGroupInsert = "INSERT INTO group_room() VALUES ()";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(conn -> conn.prepareStatement(sqlGroupInsert, new String[] {"id"}), keyHolder);

        long roomId = Objects.requireNonNull(keyHolder.getKey()).longValue();

        for (String member : memberList) {
            String sqlInsert = "INSERT INTO group_room_members(room_id, user_id) VALUES (?, ?)";
            int update = 0;
            try {
                update = jdbcTemplate.update(sqlInsert, roomId, member);
            } catch (Exception e) {
                log.error("makeNewGroup add members: exception = {}", e.getMessage());
            }
            if (update > 0) {
                resultList.add(member);
            }
        }
        return resultList;
    }

    private RowMapper<GroupChat> chatRowMapper() {
        return (rs, rowNum) -> GroupChat.builder()
                .id(rs.getLong("id"))
                .senderUserId(rs.getString("sender_user_id"))
                .roomId(rs.getLong("room_id"))
                .content(rs.getString("content"))
                .createdAt(rs.getTimestamp("created_at"))
                .build();
    }

    private RowMapper<Pair<Long, Long>> groupLastMessageRowMapper() {
        return (rs, rowNum) -> new Pair<>(
                rs.getLong("room_id"),
                rs.getLong("max_id"));
    }

    private RowMapper<Pair<String, Timestamp>> readStatusRowMapper() {
        return (rs, rowNum) -> new Pair<>(
                rs.getString("user_id"),
                rs.getTimestamp("read_at"));
    }

    private RowMapper<String> groupMemberRowMapper() {
        return (rs, rowNum) -> rs.getString("user_id");
    }
}
