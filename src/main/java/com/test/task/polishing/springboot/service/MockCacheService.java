package com.test.task.polishing.springboot.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockCacheService {
    private static MockCacheService instance;

    private List<String> languages = new ArrayList<>();
    private List<String> domains = new ArrayList<>();

    private MockCacheService() {
    }

    public static synchronized MockCacheService getInstance() {

        if (instance == null) {
            instance = new MockCacheService();
        }

        return instance;
    }

    public synchronized void addLanguages(String[] languages) {
        this.languages = new ArrayList<>(Arrays.asList(languages));
    }

    public synchronized void addDomains(String[] domains) {
        this.domains = new ArrayList<>(Arrays.asList(domains));
    }

    public synchronized boolean existsLanguage(String language) {
        return languages.contains(language);
    }

    public synchronized boolean existsDomain(String domain) {
        return domains.contains(domain);
    }
}