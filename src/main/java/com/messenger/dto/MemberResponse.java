package com.messenger.dto;

import com.messenger.domain.Member;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberResponse {

    private final String id;
    private final String name;
    private final String statusMessage;

    private MemberResponse(String id, String name, String statusMessage) {
        this.id = id;
        this.name = name;
        this.statusMessage = statusMessage;
    }

    public static MemberResponse of(@NonNull Member member) {
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getStatusMessage());
    }

    public static List<MemberResponse> of(@NonNull List<Member> members) {
        return members.stream().map(MemberResponse::of).collect(Collectors.toList());
    }
}
