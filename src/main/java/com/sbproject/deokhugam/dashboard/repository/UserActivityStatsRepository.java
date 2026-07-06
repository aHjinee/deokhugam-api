package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface UserActivityStatsRepository
	extends MongoRepository<UserActivityStatsDocument, String> {

	// 현재 30일 주기에 포함되는 문서 조회
	@Query(
		value = """
			{
				'user_id': ?0,
				'activity_date': {
					'$gte': ?1,
					'$lt': ?2
				}
			}
			""",
		sort = "{ 'activity_date': 1 }"
	)
	List<UserActivityStatsDocument>
	findActivityStatsByPeriod(
		String userId,
		Instant startDate,
		Instant endDate
	);
}