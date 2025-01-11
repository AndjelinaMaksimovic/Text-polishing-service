package com.test.task.polishing.springboot.dto;

import lombok.Data;

@Data
public class PolishingResponseDto {
    private String polished_content;
    private double similarity;
}
