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
        Member member = Member.builder("id1", "pw1").name("name1").build();

        //when
        boolean result = memberService.signup(member);

        //then
        assertThat(result).isEqualTo(true);
        Member findMember = memberService.findById(member.getId()).get();
        assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void duplicatedMember() {
        //given
        Member member1 = Member.builder("id1", "pw1").name("name1").build();
        Member member2 = Member.builder("id1", "pw2").name("name2").build();

        //when
        boolean result1 = memberService.signup(member1);
        boolean result2 = memberService.signup(member2);

        //then
        assertThat(result1).isEqualTo(true);
        assertThat(result2).isEqualTo(false);
        Member findMember = memberService.findById(member1.getId()).get();
        assertThat(findMember).isEqualTo(member1);
    }


}
