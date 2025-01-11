package com.test.task.polishing.springboot.dto;

import lombok.Data;

@Data
public class PolishingRequestDto {
    private String language;
    private String domain;
    private String content;
}