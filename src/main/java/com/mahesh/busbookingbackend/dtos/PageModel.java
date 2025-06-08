package com.mahesh.busbookingbackend.dtos;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PageModel {

    private Integer pageNumber;
    private Integer recordSize;
    private String sortColumn;
    private OrderBy orderBy;
}
