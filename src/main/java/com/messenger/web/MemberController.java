package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.repository.MemberRepository;
import com.messenger.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MemberController {

    private final MemberService memberService;

    @Autowired
    public MemberController(MemberRepository memberRepository, MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 전체회원 목록
     * @return 전체회원 객체를 List로 반환
     */
    @GetMapping("/api/members")
    public List<Member> members() {
        List<Member> members = memberService.memberList();
        return members;
    }

    /**
     * 회원 가입
     * @param id        가입할 회원 id
     * @param password  가입할 회원 비밀번호
     * @param name      가입할 회원 이름(생략 가능)
     * @return  정상적으로 가입된 경우 : 가입된 회원 객체
     *          그 외 : null
     */
    @PostMapping(value = "/api/member/signup", consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<Member> signup(@RequestParam String id,
                                         @RequestParam String password,
                                         @RequestParam(required = false, defaultValue = "") String name) {
        Member member = new Member(id, password, name);
        boolean ret = memberService.memberSignup(member);
        if (!ret) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>(member, HttpStatus.OK);
    }

}
