package com.messenger.web;

import com.messenger.domain.Member;
import com.messenger.dto.DefaultResponse;
import com.messenger.dto.MemberResponse;
import com.messenger.service.MemberService;
import com.messenger.util.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.List;

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
    @Operation(summary = "전체 회원 목록", security = {@SecurityRequirement(name = "authorization")})
    public DefaultResponse<List<MemberResponse>> members() {
        return DefaultResponse.ofSuccess(MemberResponse.of(memberService.listAll()));
    }

    /**
     * 회원 가입
     * @param id        가입할 회원 id
     * @param password  가입할 회원 비밀번호
     * @param name      가입할 회원 이름(생략 가능)
     * @return  정상적으로 가입된 경우 : 가입된 회원 객체
     *          그 외 : null
     */
    @PostMapping(value = "/api/v1/members")
    @Operation(summary = "회원 가입", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "id", description = "회원 id", required = true),
            @Parameter(name = "password", description = "회원 비밀번호", required = true),
            @Parameter(name = "name", description = "회원 이름")
    })
    public DefaultResponse<MemberResponse> signup(@RequestParam String id,
                                                  @RequestParam String password,
                                                  @RequestParam(required = false) String name) {
        Member member = Member.builder()
                                .id(id)
                                .password(password)
                                .name(name)
                                .build();
        Member result = memberService.signup(member);

        return DefaultResponse.ofSuccess(MemberResponse.of(result));
    }

    /**
     * id로 회원 조회
     * @param memberId 조회할 회원 id
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/{memberId}")
    @Operation(summary = "id로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "memberId", description = "회원 id", required = true)
    })
    public DefaultResponse<MemberResponse> findById(@PathVariable String memberId) {
        Member findMember = memberService.findById(memberId);
        return DefaultResponse.ofSuccess(MemberResponse.of(findMember));
    }

    /**
     * 이름으로 회원 조회
     * @param memberName 조회할 회원 이름
     * @return  조회된 경우 : 조회된 회원 객체
     *          그 외 : null
     */
    @GetMapping("/api/v1/members/name/{memberName}")
    @Operation(summary = "이름으로 회원 조회", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "memberName", description = "회원 이름", required = true)
    })
    public DefaultResponse<List<MemberResponse>> findByName(@PathVariable String memberName) {
        List<Member> findMemberList = memberService.findByName(memberName);
        return DefaultResponse.ofSuccess(MemberResponse.of(findMemberList));
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
    @PutMapping(value = "/api/v1/members/{memberId}")
    @Operation(summary = "회원 정보 변경", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "memberId", description = "회원 id", required = true),
            @Parameter(name = "name", description = "변경할 이름"),
            @Parameter(name = "password", description = "변경할 비밀번호"),
            @Parameter(name = "content", description = "변경할 상태 메시지")
    })
    public DefaultResponse<MemberResponse> updateInfo(@PathVariable String memberId,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String password,
                                                      @RequestParam(required = false) String content) {
        log.debug("memberId={}, name={}, password={}", memberId, name, password);
        Member result;
        result = memberService.updateInfo(
                Member.builder()
                        .id(memberId)
                        .password(password)
                        .name(name)
                        .statusMessage(content)
                        .build());
        return DefaultResponse.ofSuccess(MemberResponse.of(result));
    }

    @PostMapping(value = "/api/v1/members/login")
    @Operation(summary = "회원 로그인", security = {@SecurityRequirement(name = "authorization")})
    @Parameters({
            @Parameter(name = "id", description = "회원 id", required = true),
            @Parameter(name = "password", description = "비밀번호", required = true)
    })
    public ResponseEntity<DefaultResponse<MemberResponse>> login(@RequestParam String id,
                                                                 @RequestParam String password,
                                                                 HttpSession session) {

        logForSession(session);
        Pair<Member, HttpHeaders> pair = memberService.login(id, password);
        Member findMember = pair.getFirst();
        HttpHeaders httpHeaders = pair.getSecond();

        // 헤더에 jwt 토큰을 넣어주기 위해서 ResponseEntity 사용
        return new ResponseEntity<>(DefaultResponse.ofSuccess(MemberResponse.of(findMember)), httpHeaders, HttpStatus.OK);
    }

    @PostMapping(value = "/api/v1/members/logout")
    @Operation(summary = "회원 로그아웃", security = {@SecurityRequirement(name = "authorization")})
    public DefaultResponse<Void> logout(HttpSession session) {
        logForSession(session);

        // TODO: 로그아웃 한 경우, 기존 jwt 토큰 처리 필요
        return DefaultResponse.ofSuccess();
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