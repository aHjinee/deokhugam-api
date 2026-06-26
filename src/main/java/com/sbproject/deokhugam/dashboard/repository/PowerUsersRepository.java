package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.PowerUsersDocument;
import com.sbproject.deokhugam.dashboard.entity.PeriodType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PowerUsersRepository extends MongoRepository<PowerUsersDocument, String> {

  Optional<PowerUsersDocument> findTopByPeriodTypeOrderByPeriodDateDesc(PeriodType periodType);
}