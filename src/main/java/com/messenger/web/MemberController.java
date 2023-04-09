package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.exception.MyException;
import com.messenger.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import com.messenger.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import static com.messenger.util.DateTimeConvertor.convertTimestampMillis2String;

@Slf4j
@RestController
public class MemberController {

    public static final String SESSION_KEY_USER_ID = "USER_ID";
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 전체회원 목록
     * @return 전체회원 객체를 List로 반환
     */
    @GetMapping("/api/v1/members")
    public List<Member> members() {
        return memberService.listAll();
    }

    /**
     * 회원 가입
     * @param id        가입할 회원 id
     * @param password  가입할 회원 비밀번호
     * @param name      가입할 회원 이름(생략 가능)
     * @return  정상적으로 가입된 경우 : 가입된 회원 객체
     *          그 외 : null
     */
    @PostMapping(value = "/api/v1/members", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> signup(@RequestParam String id,
                                         @RequestParam String password,
                                         @RequestParam(required = false) String name) {
        Member member = Member.builder()
                                .id(id)
                                .password(password)
                                .name(name)
                                .build();
        Member result;
        try {
            result = memberService.signup(member);
        } catch (MyException e) {
            return new ResponseEntity<>(null, e.errorCode.httpStatusCode);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * id로 회원 조회
     * @param memberId 조회할 회원 id
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/{memberId}")
    public ResponseEntity<Member> findById(@PathVariable String memberId) {
        Optional<Member> findMember = memberService.findById(memberId);
        return new ResponseEntity<>(findMember.orElse(null), HttpStatus.OK);
    }

    /**
     * 이름으로 회원 조회
     * @param memberName 조회할 회원 이름
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/name/{memberName}")
    public ResponseEntity<List<Member>> findByName(@PathVariable String memberName) {
        List<Member> findMember = memberService.findByName(memberName);
        return new ResponseEntity<>(findMember, HttpStatus.OK);
    }

    /**
     * 회원 정보 변경
     * @param memberId  회원 id
     * @param name      변경할 이름
     * @param password  변경할 비밀번호
     * @param content   변경할 회원 상태 메시지
     * @return  변경된 경우 : 변경된 회원 객체
     *          그 외 : null
     */
    @PutMapping(value = "/api/v1/members/{memberId}", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> updateInfo(@PathVariable String memberId,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String password,
                                             @RequestParam(required = false) String content) {
        log.debug("memberId={}, name={}, password={}", memberId, name, password);
        Member result;
        try {
            result = memberService.updateInfo(
                    Member.builder()
                            .id(memberId)
                            .password(password)
                            .name(name)
                            .statusMessage(content)
                            .build());
        } catch (MyException e) {
            return new ResponseEntity<>(null, e.errorCode.httpStatusCode);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/members/login", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> login(@RequestParam String id,
                                        @RequestParam String password,
                                        HttpSession session) {

        logForSession(session);
        Pair<Member, HttpHeaders> pair;
        try {
            pair = memberService.login(id, password);
        } catch (MyException e) {
            return new ResponseEntity<>(null, e.errorCode.httpStatusCode);
        }
        Member findMember = pair.getFirst();
        HttpHeaders httpHeaders = pair.getSecond();
        return new ResponseEntity<>(findMember, httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/members/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        logForSession(session);
        session.removeAttribute(SESSION_KEY_USER_ID);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private static void logForSession(HttpSession session) {
        log.debug("session id={}", session.getId());
        log.debug("session CreationTime={}", convertTimestampMillis2String(session.getCreationTime()));
        log.debug("session LastAccessedTime={}", convertTimestampMillis2String(session.getLastAccessedTime()));

        Enumeration<String> sessionNames = session.getAttributeNames();
        while (sessionNames.hasMoreElements()) {
            String sessionName = sessionNames.nextElement();
            log.debug("session key={}, value={}", sessionName, session.getAttribute(sessionName));
        }
    }

}