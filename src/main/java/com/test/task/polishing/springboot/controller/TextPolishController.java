package com.test.task.polishing.springboot.controller;

import com.test.task.polishing.springboot.dto.PolishingResponseDto;
import com.test.task.polishing.springboot.dto.PolishingRequestDto;
import com.test.task.polishing.springboot.service.TextPolishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TextPolishController {

    private final TextPolishService textPolishService;

    @PostMapping("/polish")
    PolishingResponseDto polishText(@RequestBody PolishingRequestDto polishingRequestDto) {
        return textPolishService.polishText(polishingRequestDto);
    }

}