package com.messenger.dto.pagination;

import com.messenger.domain.Chat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class PaginationWrapper<T extends Pageable> {

    long nextId = -1;
    Chat latestReceivedChat;
    int size;
    List<T> list;

    private PaginationWrapper(List<T> list) {
        this.list = list;
        int length = list.size();
        if (length > 0) {
            nextId = list.get(length - 1).getId();
        }
        this.size = length;
    }

    public static <T extends Pageable> PaginationWrapper<T> of(List<T> list) {
        return new PaginationWrapper<>(list);
    }
}
