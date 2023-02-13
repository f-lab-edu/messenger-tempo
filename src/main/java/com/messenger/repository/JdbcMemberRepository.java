package com.messenger.repository;

import com.messenger.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
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
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // 트랜잭션 시작
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            int ret = pstmt.executeUpdate();
            if (ret != 1) {
                throw new SQLException("executeUpdate return: "+ret);
            }
            conn.commit();  // 트랜잭션 커밋
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 트랜잭션 롤백
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return false;
        } finally {
            close(conn, pstmt, rs);
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
                String password = rs.getString("pw");
                String displayName = rs.getString("display_name");
                Member member = new Member(memberId, password, displayName);
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
                String password = rs.getString("pw");
                String displayName = rs.getString("display_name");
                Member member = new Member(memberId, password, displayName);
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
                String password = rs.getString("pw");
                String displayName = rs.getString("display_name");
                Member member = new Member(memberId, password, displayName);
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
     * 회원 비밀번호 업데이트
     * @param id        회원 id
     * @param password  변경할 회원 비밀번호
     * @return 업데이트 성공 여부
     */
    @Override
    public boolean updatePassword(String id, String password) {
        Optional<Member> findMember = findById(id);
        if (findMember.isEmpty()) {
            return false;
        }
        String sql = "UPDATE member SET pw = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // 트랜잭션 시작
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, password);
            pstmt.setString(2, id);
            int ret = pstmt.executeUpdate();
            if (ret != 1) {  // 이전과 동일한 경우도 예외 발생하게 처리
                throw new SQLException("executeUpdate return: "+ret);
            }
            conn.commit();  // 트랜잭션 커밋
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 트랜잭션 롤백
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return false;
        } finally {
            close(conn, pstmt, rs);
        }
    }

    /**
     * 회원 이름 업데이트
     * @param id    회원 id
     * @param name  변경할 회원 이름
     * @return 업데이트 성공 여부
     */
    @Override
    public boolean updateDisplayName(String id, String name) {
        Optional<Member> findMember = findById(id);
        if (findMember.isEmpty()) {
            return false;
        }
        String sql = "UPDATE member SET display_name = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);  // 트랜잭션 시작
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            int ret = pstmt.executeUpdate();
            if (ret != 1) {  // 이전과 동일한 경우도 예외 발생하게 처리
                throw new SQLException("executeUpdate return: "+ret);
            }
            conn.commit();  // 트랜잭션 커밋
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();  // 트랜잭션 롤백
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            return false;
        } finally {
            close(conn, pstmt, rs);
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
