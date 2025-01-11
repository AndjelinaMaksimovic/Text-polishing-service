package com.test.task.polishing.springboot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CronJobService {

    @Value("${proofread.baseurl}")
    private String baseUrl;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "${fetching.validations.cron}")
    public void getValidLanguages() throws JsonProcessingException {

        String apiUrl = baseUrl + "/languages";
        String[] validLanguages = (restTemplate.getForEntity(apiUrl, String[].class)).getBody();

        MockCacheService.getInstance().addLanguages(validLanguages);

        assert validLanguages != null;
        log.info("Valid languages just fetched from proofreading service: {}",
                String.join(", ", validLanguages));
    }

    @Scheduled(cron = "${fetching.validations.cron}")
    public void getValidDomains() throws JsonProcessingException {

        String apiUrl = baseUrl + "/domains";
        String[] validDomains = (restTemplate.getForEntity(apiUrl, String[].class)).getBody();

        MockCacheService.getInstance().addDomains(validDomains);

        assert validDomains != null;
        log.info("Valid domains just fetched from proofreading service: {}",
                String.join(", ", validDomains));

    }

}