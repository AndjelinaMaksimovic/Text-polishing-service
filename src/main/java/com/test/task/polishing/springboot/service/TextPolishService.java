package com.test.task.polishing.springboot.service;

import com.test.task.polishing.springboot.dto.PolishingRequestDto;
import com.test.task.polishing.springboot.dto.PolishingResponseDto;
import com.test.task.polishing.springboot.dto.ProofreadContentResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.StringTokenizer;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextPolishService {


    private final RestTemplate restTemplate;
    @Value("${proofread.baseurl}")
    private String baseUrl;

    public PolishingResponseDto polishText(@Valid PolishingRequestDto polishingRequestDto) {

        if (validateRequestForProofreading(polishingRequestDto)) {
            String apiUrl = baseUrl + "/proofread";
            String proofreadContent = (Objects.requireNonNull(restTemplate
                    .postForObject(apiUrl, polishingRequestDto, ProofreadContentResponseDto.class))).getProofread_content();

            PolishingResponseDto responseDto = new PolishingResponseDto();
            responseDto.setPolished_content(proofreadContent);
            responseDto.setSimilarity(calculateSimilarity(removeTags(polishingRequestDto.getContent()), proofreadContent));

            return responseDto;
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST
        );
    }

    private boolean validateRequestForProofreading(PolishingRequestDto polishingRequestDto) {

        return languageMatch(polishingRequestDto.getLanguage()) &&
                domainMatch(polishingRequestDto.getDomain()) &&
                lengthMatch(polishingRequestDto.getContent());

    }

    private boolean languageMatch(String language) {

        boolean validLanguage = language != null && MockCacheService.getInstance().existsLanguage(language);

        if (!validLanguage) {
            log.warn("Requested language {} is not supported.", language);
        }

        return validLanguage;
    }

    private boolean domainMatch(String domain) {

        boolean validDomain = domain != null && MockCacheService.getInstance().existsDomain(domain);

        if (!validDomain) {
            log.warn("Requested domain {} is not supported.", domain);
        }

        return validDomain;
    }

    private boolean lengthMatch(String content) {

        boolean validContentLength = (new StringTokenizer(content).countTokens()) <= 30;

        if (!validContentLength)
            log.warn("The maximum length of 30 characters has been exceeded.");

        return validContentLength;
    }

    private double calculateSimilarity(String content, String proofreadContent) {
        JaroWinklerSimilarity jaroWinkler = new JaroWinklerSimilarity();
        return round(jaroWinkler.apply(content, proofreadContent), 2);
    }

    private double round(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private String removeTags(String content) {

        if (content == null) {
            return null;
        }
        return content.replaceAll("<[^>]+>", "");
    }

}
