package com.messenger.repository;

import com.messenger.domain.Member;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class JdbcMemberRepository implements MemberRepository {

    private final DataSource dataSource;
    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * 회원정보를 저장소에 저장
     * @param member 저장할 회원 객체
     * @return 저장 성공 여부
     */
    public boolean save(Member member) {
        String sql = "INSERT INTO member(id, pw, display_name) values(?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.executeUpdate();
            return true;
        } catch (DuplicateKeyException e) {
            throw new MyException(ErrorCode.ALREADY_EXIST_ID);
        } catch (Exception e) {
            return false;
        } finally {
            close(conn, pstmt, null);
        }
    }

    /**
     * 저장소에서 id 기반으로 회원 검색
     * @param id 검색할 회원 id
     * @return (nullable)검색된 회원 객체
     */
    public Optional<Member> findById(String id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String memberId = rs.getString("id");
                String memberPw = rs.getString("pw");
                String displayName = rs.getString("display_name");
                String statusMessage = rs.getString("status_message");
                Member member = new Member(memberId, memberPw, displayName, statusMessage);
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * 저장소에서 이름 기반으로 회원 검색
     * @param name 검색할 회원 이름
     * @return 검색된 회원 객체의 List
     */
    @Override
    public List<Member> findByName(String name) {
        String sql = "SELECT * FROM member WHERE display_name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                String memberId = rs.getString("id");
                String memberPw = rs.getString("pw");
                String displayName = rs.getString("display_name");
                String statusMessage = rs.getString("status_message");
                Member member = new Member(memberId, memberPw, displayName, statusMessage);
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * 회원 목록
     * @return 회원 객체의 List
     */
    @Override
    public List<Member> findAll() {
        String sql = "SELECT * FROM member";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            List<Member> members = new ArrayList<>();
            while (rs.next()) {
                String memberId = rs.getString("id");
                String memberPw = rs.getString("pw");
                String displayName = rs.getString("display_name");
                String statusMessage = rs.getString("status_message");
                Member member = new Member(memberId, memberPw, displayName, statusMessage);
                members.add(member);
            }
            return members;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public Optional<Member> findByIdAndPw(String id, String password) {
        String sql = "SELECT * FROM member WHERE id = ? AND pw = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, id);
            pstmt.setString(2, password);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                String memberId = rs.getString("id");
                String memberPw = rs.getString("pw");
                String displayName = rs.getString("display_name");
                String statusMessage = rs.getString("status_message");
                Member member = new Member(memberId, memberPw, displayName, statusMessage);
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * 회원 정보 변경
     * @param paramMember 변경할 회원 정보 객체
     * @return 변경 성공 여부
     */
    @Override
    public boolean updateMember(Member paramMember) {
        String sql = "UPDATE member SET pw = ?, display_name = ?, content = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            log.debug("UPDATE member SET pw = {}, display_name = {}, content = {} WHERE id = {}",
                    paramMember.getPassword(),
                    paramMember.getName(),
                    paramMember.getStatusMessage(),
                    paramMember.getId());
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, paramMember.getPassword());
            pstmt.setString(2, paramMember.getName());
            pstmt.setString(3, paramMember.getStatusMessage());
            pstmt.setString(4, paramMember.getId());
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            close(conn, pstmt, null);
        }
    }

    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }
}