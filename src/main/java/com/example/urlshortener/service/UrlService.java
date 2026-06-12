package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class UrlService {

    @Autowired
    private UrlRepository urlRepository;

    public Url saveUrl(Map<String, Object> request) {

        String originalUrl = (String) request.get("url");

        String customCode = (String) request.get("customCode");

        Integer expiryDays =
                request.get("expiryDays") == null
                        ? 7
                        : (Integer) request.get("expiryDays");

        Url url = new Url();

        url.setOriginalUrl(originalUrl);

        String shortCode;

        if (customCode != null && !customCode.isBlank()) {

            if (urlRepository.findByShortCode(customCode).isPresent()) {
                throw new RuntimeException("Short code already exists");
            }

            shortCode = customCode;

        } else {

            shortCode =
                    UUID.randomUUID().toString().substring(0, 5);
        }

        url.setShortCode(shortCode);

        url.setClickCount(0);

        url.setExpiryDate(
                LocalDateTime.now().plusDays(expiryDays)
        );

        return urlRepository.save(url);
    }

    public Url findByCode(String shortCode) {

        return urlRepository.findByShortCode(shortCode)
                .orElse(null);
    }

    public String getOriginalUrl(String shortCode) {

        Url url = urlRepository.findByShortCode(shortCode)
                .orElse(null);

        if (url != null) {

            if (url.getExpiryDate().isBefore(LocalDateTime.now())) {
                return null;
            }

            url.setClickCount(url.getClickCount() + 1);

            urlRepository.save(url);

            return url.getOriginalUrl();
        }

        return null;
    }
}