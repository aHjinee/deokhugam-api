package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.PopularReviewsDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PopularReviewsRepository extends MongoRepository<PopularReviewsDocument, String> {

  Optional<PopularReviewsDocument> findTopByPeriodTypeOrderByPeriodDateDesc (PeriodType periodType);
}