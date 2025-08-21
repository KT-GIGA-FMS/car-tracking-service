package com.kt_giga_fms.car_tracking.dto;

import java.util.List;
import lombok.Getter;

@Getter
public class ApiListResponse<T> {
    private final List<T> items;
    private final int count;

    private ApiListResponse(List<T> items) {
        this.items = items;
        this.count = items != null ? items.size() : 0;
    }

    public static <T> ApiListResponse<T> of(List<T> items) {
        return new ApiListResponse<>(items);
    }
}
