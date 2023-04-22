package com.messenger.dto.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "페이지네이션 RequestDTO")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestPagination {

    @Schema(description = "이전 조회한 마지막 메시지 id")
    Integer nextId;

    @Schema(description = "조회할 메시지 개수", defaultValue = "3")
    Integer size = 3;
}
