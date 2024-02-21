package com.backend.aiblog.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PaginationResponse {

    private List<?> data;

    private Integer previousCursor;

    private Integer nextCursor;
}
