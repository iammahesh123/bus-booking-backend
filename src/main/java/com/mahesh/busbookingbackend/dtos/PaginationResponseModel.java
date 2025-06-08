package com.mahesh.busbookingbackend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@ToString
@Schema
@NoArgsConstructor
@AllArgsConstructor
public class PaginationResponseModel<T> {
    private long totalPages;
    private long totalRecords;
    private List<T> data;
}
