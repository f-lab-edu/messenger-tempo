package com.messenger.repository;

import com.messenger.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class JdbcTemplateMemberRepository implements MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateMemberRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<Member> memberRowMapper() {
        return (rs, rowNum) -> Member.builder(rs.getString("id"), rs.getString("pw"))
                .name(rs.getString("display_name"))
                .statusMessage(rs.getString("status_message"))
                .build();
    }

    public boolean save(Member member) {
        String sql = "INSERT INTO member(id, pw, display_name, status_message) values(?, ?, ?, ?)";
        log.debug("member={}", member);
        Object[] args = {
                member.getId(),
                member.getPassword(),
                member.getName(),
                member.getStatusMessage()
        };
        int update = -1;
        try {
            update = jdbcTemplate.update(sql, args);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        log.debug("update={}", update);
        return update == 1;
    }

    public Optional<Member> findById(String id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), id);
        return result.stream().findAny();
    }

    @Override
    public List<Member> findByName(String name) {
        String sql = "SELECT * FROM member WHERE display_name = ?";
        return jdbcTemplate.query(sql, memberRowMapper(), name);
    }

    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM member";
        return jdbcTemplate.query(sql, memberRowMapper());
    }

    @Override
    public Optional<Member> findByIdAndPw(String id, String password) {
        String sql = "SELECT * FROM member WHERE id = ? AND pw = ?";
        List<Member> result = jdbcTemplate.query(sql, memberRowMapper(), id, password);
        return result.stream().findAny();
    }

    @Override
    public boolean updateMember(Member paramMember) {
        String sql = "UPDATE member SET pw = ?, display_name = ?, status_message = ? WHERE id = ?";
        log.debug("paramMember={}", paramMember);
        Object[] args = {
                paramMember.getPassword(),
                paramMember.getName(),
                paramMember.getStatusMessage(),
                paramMember.getId()
        };
        int update = -1;
        try {
            update = jdbcTemplate.update(sql, args);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        log.debug("update={}", update);
        return update == 1;
    }
}
