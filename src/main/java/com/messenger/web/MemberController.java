package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.repository.MemberRepository;
import com.messenger.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
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
     * id로 회원 조회
     * @param memberId 조회할 회원 id
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/members/id/{memberId}")
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
    @PostMapping(value = "/api/v1/members/id/{memberId}", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> updateMemberInfo(@PathVariable String memberId,
                                                   @RequestParam(required = false) String name,
                                                   @RequestParam(required = false) String password,
                                                   @RequestParam(required = false) String content) {
        log.debug("memberId={}, name={}, password={}", memberId, name, password);

        // memberId를 찾을 수 없는 경우
        Optional<Member> findMember = memberService.findMemberById(memberId);
        if (findMember.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if (name != null && !name.equals("")) {
            boolean result = memberService.updateMemberName(memberId, name);
            if (!result) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (password != null && !password.equals("")) {
            boolean result= memberService.updateMemberPassword(memberId, password);
            if (!result) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        if (content != null && !content.equals("")) {
            boolean result = memberService.updateMemberContent(memberId, content);
            if (!result) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // 변경된 후 다시 memberId를 찾는다
        Member findMemberAfter = memberService.findMemberById(memberId).get();
        // 변경된 것이 하나도 없는 경우
        if (findMemberAfter.equals(findMember.get())) {
            return new ResponseEntity<>(null, HttpStatus.NOT_MODIFIED);
        }

        return new ResponseEntity<>(findMemberAfter, HttpStatus.OK);
    }

}