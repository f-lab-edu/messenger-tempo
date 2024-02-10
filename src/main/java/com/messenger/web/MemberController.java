package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.member.*;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.service.MemberService;
import com.messenger.util.Pair;
import com.messenger.validator.MemberValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;
    private final MemberValidator memberValidator;

    @InitBinder
    public void init(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(memberValidator);
    }

    public MemberController(MemberService memberService, MemberValidator memberValidator) {
        this.memberService = memberService;
        this.memberValidator = memberValidator;
    }

    @GetMapping("/api/v1/members")
    @Operation(summary = "전체 회원 목록", security = {@SecurityRequirement(name = "authorization")})
    public List<MemberResponse> members() {
        return memberService.listAll();
    }

    @PostMapping(value = "/api/v1/members")
    @Operation(summary = "회원 가입", security = {@SecurityRequirement(name = "authorization")})
    public MemberResponse signup(@RequestBody MemberSignupRequest request, BindingResult bindingResult) {

        memberValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            log.error("member signup validation error: {}", bindingResult.getFieldError());
            throw new MyException(ErrorCode.VALIDATION_FAIL);
        }

        Member result = memberService.signup(request);
        return MemberResponse.of(result);
    }

    @GetMapping("/api/v1/members/{memberId}")
    @Operation(summary = "id로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "memberId", description = "회원 id", required = true)
    public MemberResponse findById(@PathVariable String memberId) {

        if (!MemberValidator.validateId(memberId)) {
            log.error("member findById validation error: id = {}", memberId);
            throw new MyException(ErrorCode.VALIDATION_FAIL);
        }

        Member findMember = memberService.findById(memberId);
        return MemberResponse.of(findMember);
    }

    @GetMapping("/api/v1/members/name/{memberName}")
    @Operation(summary = "이름으로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameter(name = "memberName", description = "회원 이름", required = true)
    public List<MemberResponse> findByName(@PathVariable String memberName) {

        if (!MemberValidator.validateName(memberName)) {
            log.error("member findByName validation error: name = {}", memberName);
            throw new MyException(ErrorCode.VALIDATION_FAIL);
        }

        List<Member> findMemberList = memberService.findByName(memberName);
        return MemberResponse.newList(findMemberList);
    }

    @PutMapping(value = "/api/v1/members")
    @Operation(summary = "회원 정보 변경", security = {@SecurityRequirement(name = "authorization")})
    public MemberResponse updateInfo(@RequestBody MemberUpdateInfoRequest request,
                                     BindingResult bindingResult) {

        memberValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            log.error("member updateInfo validation error: {}", bindingResult.getFieldError());
            throw new MyException(ErrorCode.VALIDATION_FAIL);
        }

        Member result = memberService.updateInfo(request);
        return MemberResponse.of(result);
    }

    @PostMapping(value = "/api/v1/members/login")
    @Operation(summary = "회원 로그인", security = {@SecurityRequirement(name = "authorization")})
    public MemberLoginResponse login(@RequestBody MemberLoginRequest request,
                                     HttpServletResponse response,
                                     BindingResult bindingResult) {

        memberValidator.validate(request, bindingResult);
        if (bindingResult.hasErrors()) {
            log.error("member login validation error: {}", bindingResult.getFieldError());
            throw new MyException(ErrorCode.VALIDATION_FAIL);
        }

        Pair<MemberLoginResponse, Cookie> pair = memberService.login(request);
        MemberLoginResponse memberResponse = pair.getFirst();

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