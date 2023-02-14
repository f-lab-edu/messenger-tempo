package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 전체회원 목록
     * @return 전체회원 객체를 List로 반환
     */
    @GetMapping("/api/v1/members")
    public List<Member> members() {
        return memberService.listMember();
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
                                         @RequestParam(required = false, defaultValue = "") String name) {
        Member member = new Member(id, password, name);
        boolean ret = memberService.signupMember(member);
        if (!ret) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

    /**
     * id로 회원 조회
     * @param memberId 조회할 회원 id
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/id/{memberId}")
    public ResponseEntity<Member> findMemberById(@PathVariable String memberId) {
        Optional<Member> findMember = memberService.findMemberById(memberId);

        if (findMember.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(findMember.get(), HttpStatus.OK);
    }

    /**
     * 이름으로 회원 조회
     * @param memberName 조회할 회원 이름
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/name/{memberName}")
    public ResponseEntity<List<Member>> findMemberByName(@PathVariable String memberName) {
        List<Member> findMember = memberService.findMemberByName(memberName);

        if (findMember.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
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
    @PutMapping(value = "/api/v1/members/id/{memberId}", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> updateMemberInfo(@PathVariable String memberId,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String password,
                                                   @RequestParam(required = false) String content) {
        log.debug("memberId={}, name={}, password={}", memberId, name, password);

        // memberId를 찾을 수 없는 경우
        Member findMember = memberService.findMemberById(memberId).orElse(null);
        if (findMember == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        Member paramMember = new Member(memberId, password, name);
        paramMember.setStatusMessage(content);

        boolean result = memberService.updateMemberInfo(paramMember);
        if (!result) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 변경된 후 다시 memberId를 찾는다
        Member findMemberAfter = memberService.findMemberById(memberId).orElseThrow();
        // 변경된 것이 하나도 없는 경우
        if (findMemberAfter.equals(findMember)) {
            return new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(findMemberAfter, HttpStatus.OK);
    }

}