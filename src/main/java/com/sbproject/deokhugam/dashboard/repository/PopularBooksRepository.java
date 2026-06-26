package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.PopularBooksDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PopularBooksRepository extends MongoRepository<PopularBooksDocument, String> {

  Optional<PopularBooksDocument> findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType periodType);
}