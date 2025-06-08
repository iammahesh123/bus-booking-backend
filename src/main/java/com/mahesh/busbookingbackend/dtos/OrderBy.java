package com.mahesh.busbookingbackend.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema
@Getter
public enum OrderBy {
    ASC, DESC
}
