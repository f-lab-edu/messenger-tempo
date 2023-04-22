package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.member.*;
import com.messenger.service.MemberService;
import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/api/v1/members")
    @Operation(summary = "전체 회원 목록", security = {@SecurityRequirement(name = "authorization")})
    public List<ResponseMember> members() {
        return ResponseMember.of(memberService.listAll());
    }

    @PostMapping(value = "/api/v1/members")
    @Operation(summary = "회원 가입", security = {@SecurityRequirement(name = "authorization")})
    public ResponseMember signup(@RequestBody RequestMemberSignup request) {

        Member result = memberService.signup(request);
        return ResponseMember.of(result);
    }

    @GetMapping("/api/v1/members/{memberId}")
    @Operation(summary = "id로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "memberId", description = "회원 id", required = true)
    public ResponseMember findById(@PathVariable String memberId) {

        Member findMember = memberService.findById(memberId);
        return ResponseMember.of(findMember);
    }

    @GetMapping("/api/v1/members/name/{memberName}")
    @Operation(summary = "이름으로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "memberName", description = "회원 이름", required = true)
    public List<ResponseMember> findByName(@PathVariable String memberName) {

        List<Member> findMemberList = memberService.findByName(memberName);
        return ResponseMember.of(findMemberList);
    }

    @PutMapping(value = "/api/v1/members")
    @Operation(summary = "회원 정보 변경", security = {@SecurityRequirement(name = "authorization")})
    public ResponseMember updateInfo(@RequestBody RequestMemberUpdateInfo request) {

        Member result = memberService.updateInfo(request);
        return ResponseMember.of(result);
    }

    @PostMapping(value = "/api/v1/members/login")
    @Operation(summary = "회원 로그인", security = {@SecurityRequirement(name = "authorization")})
    public ResponseMemberLogin login(@RequestBody RequestMemberLogin request,
                                     HttpServletResponse response) {

        Pair<ResponseMemberLogin, Cookie> pair = memberService.login(request);
        ResponseMemberLogin memberResponse = pair.getFirst();

        Cookie cookie = pair.getSecond();
        response.addCookie(cookie);

        return memberResponse;
    }

    @PostMapping(value = "/api/v1/members/logout")
    @Operation(summary = "회원 로그아웃", security = {@SecurityRequirement(name = "authorization")})
    public DefaultResponse logout(HttpServletResponse response) {

        Cookie cookie = memberService.logout();
        response.addCookie(cookie);

        return DefaultResponse.ofSuccess();
    }
}