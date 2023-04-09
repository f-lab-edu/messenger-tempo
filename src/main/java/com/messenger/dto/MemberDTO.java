package com.messenger.dto;

import com.messenger.domain.Member;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberDTO {

    private final String id;
    private final String name;
    private final String statusMessage;

    private MemberDTO(@NonNull Member member) {
        this.id = member.getId();
        this.name = member.getName();
        this.statusMessage = member.getStatusMessage();
    }

    public static MemberDTO of(@NonNull Member member) {
        return new MemberDTO(member);
    }

    public static List<MemberDTO> of(@NonNull List<Member> members) {
        return members.stream().map(MemberDTO::new).collect(Collectors.toList());
    }
}
