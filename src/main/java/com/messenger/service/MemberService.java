package com.messenger.service;

import com.messenger.domain.Member;
import com.messenger.exception.ErrorCode;
import com.messenger.exception.MyException;
import com.messenger.repository.MemberRepository;
import com.messenger.web.MemberController;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member signup(Member member) throws MyException {
        Member result;
        try {
            result = memberRepository.save(member);
        } catch (MyException e) {
            throw new MyException(ErrorCode.FAIL_SIGNUP);
        }
        return result;
    }

    public List<Member> listAll() {
        return memberRepository.findAll();
    }

    public Optional<Member> findById(String id) {
        return memberRepository.findById(id);
    }

    public List<Member> findByName(String name) {
        return memberRepository.findByName(name);
    }

    public Member updateInfo(Member paramMember) throws MyException {
        Member findMember = findById(paramMember.getId()).orElseThrow(() -> new MyException(ErrorCode.NOT_FOUND_MEMBER));

        String memberId = paramMember.getId();
        String modifiedPassword = paramMember.getPassword();
        String modifiedName = paramMember.getName();
        String modifiedStatusMessage = paramMember.getStatusMessage();

        if (ObjectUtils.isEmpty(paramMember.getPassword())) {
            modifiedPassword = findMember.getPassword();
        }
        if (ObjectUtils.isEmpty(paramMember.getName())) {
            modifiedName = findMember.getName();
        }
        if (ObjectUtils.isEmpty(paramMember.getStatusMessage())) {
            modifiedStatusMessage = findMember.getStatusMessage();
        }
        Member ret;
        try {
            ret = memberRepository.updateMember(
                    Member.builder()
                            .id(memberId)
                            .password(modifiedPassword)
                            .name(modifiedName)
                            .statusMessage(modifiedStatusMessage)
                            .build());
        } catch (MyException e) {
            if(!e.errorCode.equals(ErrorCode.NOT_MODIFIED)) {
                throw new MyException(ErrorCode.FAIL_UPDATE_MEMBER);
            }
            throw e;
        }

        return ret;
    }

    public Member login(String id, String password, HttpSession session) throws MyException {

        String sessionUserId = (String) session.getAttribute(MemberController.SESSION_KEY_USER_ID);
        // 세션 값이 있으면 이미 로그인 중
        if (sessionUserId != null) {
            Optional<Member> findMemberSession = findById(sessionUserId);
            // 세션 값에 해당하는 유저를 찾지 못하면 세션 삭제
            if (findMemberSession.isEmpty()) {
                session.removeAttribute(MemberController.SESSION_KEY_USER_ID);
                throw new MyException(ErrorCode.NOT_FOUND_MEMBER);
            }
            throw new MyException(ErrorCode.ALREADY_LOGIN);
        }

        Member findMember = memberRepository.findByIdAndPw(id, password)
                // 아이디가 존재하지 않거나 비밀번호가 일치하지 않는 경우
                .orElseThrow(() -> new MyException(ErrorCode.NOT_MATCH_PASSWORD));

        // 아이디를 세션에 저장
        session.setAttribute(MemberController.SESSION_KEY_USER_ID, id);
        return findMember;
    }
}