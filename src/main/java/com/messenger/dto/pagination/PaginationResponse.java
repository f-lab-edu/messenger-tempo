package com.messenger.dto.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PaginationResponse<T extends Pageable> {

    @Schema(description = "이전 조회한 마지막 메시지 id")
    long nextId = -1;

    @Schema(description = "마지막 수신한 메시지")
    T latestReceivedChat;

    @Schema(description = "Pagination List의 크기")
    int size;

    @Schema(description = "Pagination List")
    List<T> list;

    private PaginationResponse(List<T> list) {
        this.list = list;
        int length = list.size();
        if (length > 0) {
            nextId = list.get(length - 1).getId();
        }
        this.size = length;
    }

    public static <T extends Pageable> PaginationResponse<T> of(List<T> list) {
        return new PaginationResponse<>(list);
    }
}
