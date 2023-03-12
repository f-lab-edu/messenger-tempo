package com.messenger.web;

import com.messenger.domain.Pageable;

import java.util.HashMap;
import java.util.List;

public class PaginationWrapper extends HashMap<String, Object> {

    public <T extends Pageable> PaginationWrapper(List<T> list) {
        super();
        long prevId = -1;

        int length = list.size();
        if (length > 0) {
            prevId = list.get(length - 1).getId();
        }
        put("prevId", prevId);
        put("list", list);
    }
}
