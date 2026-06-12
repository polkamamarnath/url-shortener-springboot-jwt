package com.example.urlshortener.controller;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class UrlController {

    @Autowired
    private UrlService urlService;

    @ResponseBody
    @PostMapping("/shorten")
    public Url shortenUrl(@RequestBody Map<String, Object> request) {

        return urlService.saveUrl(request);
    }

    @GetMapping("/{shortCode}")
    public String redirectUrl(@PathVariable String shortCode) {

        String originalUrl = urlService.getOriginalUrl(shortCode);

        if (originalUrl != null) {
            return "redirect:" + originalUrl;
        }

        return "redirect:/notfound";
    }

    @ResponseBody
    @GetMapping("/notfound")
    public String notFound() {
        return "URL Not Found";
    }

    @ResponseBody
    @GetMapping("/stats/{shortCode}")
    public Url getStats(@PathVariable String shortCode) {

        return urlService.findByCode(shortCode);
    }
}