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

    private MemberDTO(String id, String name, String statusMessage) {
        this.id = id;
        this.name = name;
        this.statusMessage = statusMessage;
    }

    public static MemberDTO of(@NonNull Member member) {
        return new MemberDTO(
                member.getId(),
                member.getName(),
                member.getStatusMessage());
    }

    public static List<MemberDTO> of(@NonNull List<Member> members) {
        return members.stream().map(MemberDTO::of).collect(Collectors.toList());
    }
}
