package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserActivityStatsRepository
	extends MongoRepository<UserActivityStatsDocument, String> {

	List<UserActivityStatsDocument>
	findTop30ByUserIdOrderByActivityDateDesc(String userId);
}