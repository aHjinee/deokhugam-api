package com.sbproject.deokhugam.dashboard.controller;

import com.sbproject.deokhugam.dashboard.document.ReviewTrendDocument;
import com.sbproject.deokhugam.dashboard.repository.ReviewTrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mongo-test")
public class MongoTestController {

    private final ReviewTrendRepository repository;

    @PostMapping
    public String save() {

        repository.save(
                ReviewTrendDocument.builder()
                        .date("2026-06-24")
                        .reviewCount(100L)
                        .build()
        );

        return "saved";
    }

    @GetMapping
    public Object findAll() {
        return repository.findAll();
    }
}