package com.jobhelper.careerflowapi.global.domain.dto;

import com.jobhelper.careerflowapi.global.domain.CursorProjection;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record CursorResponse<T>(
        List<T> content,
        Long nextCursor,
        boolean hasNext
) {

    public static <T extends CursorProjection> CursorResponse<T> of(List<T> content, int size) {
        Objects.requireNonNull(content, "content는 null일 수 없습니다.");
        boolean hasNext = content.size() > size;
        List<T> result = hasNext ? content.subList(0, size) : content;
        Long nextCursor = hasNext ? result.getLast().getId() : null;
        return new CursorResponse<>(result, nextCursor, hasNext);
    }

    public static <T> CursorResponse<T> empty() {
        return new CursorResponse<>(Collections.emptyList(), null, false);
    }
}
