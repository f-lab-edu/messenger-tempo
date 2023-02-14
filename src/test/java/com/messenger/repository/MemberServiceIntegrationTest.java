package com.messenger.repository;

import com.messenger.domain.Member;
import com.messenger.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceIntegrationTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;

    @Test
    public void signup() {
        //given
        Member member = new Member("id1", "pw1", "name1");

        //when
        boolean result = memberService.signupMember(member);

        //then
        assertThat(result).isEqualTo(true);
        Member findMember = memberService.findMemberById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void duplicatedMember() {
        //given
        Member member1 = new Member("id1", "pw1", "name1");
        Member member2 = new Member("id1", "pw2", "name2");

        //when
        boolean result1 = memberService.signupMember(member1);
        boolean result2 = memberService.signupMember(member2);

        //then
        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
        Member findMember = memberService.findMemberById(member1.getId()).get();
        assertThat(findMember).isEqualTo(member1);
    }


}
