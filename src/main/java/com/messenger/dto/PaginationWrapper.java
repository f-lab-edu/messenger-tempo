package com.messenger.dto;

import com.messenger.domain.Chat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginationWrapper<T extends Pageable> {

    long nextId = -1;
    Chat latestReceivedChat;
    int size;
    List<T> list;

    public PaginationWrapper(List<T> list) {
        this.list = list;
        int length = list.size();
        if (length > 0) {
            nextId = list.get(length - 1).getId();
        }
        this.size = length;
    }
}
