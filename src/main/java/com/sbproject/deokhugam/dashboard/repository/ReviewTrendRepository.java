package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.ReviewTrendDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewTrendRepository
        extends MongoRepository<ReviewTrendDocument, String> {
}