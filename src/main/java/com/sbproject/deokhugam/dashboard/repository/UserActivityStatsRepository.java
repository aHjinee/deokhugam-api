package com.sbproject.deokhugam.dashboard.repository;

import com.sbproject.deokhugam.dashboard.document.UserActivityStatsDocument;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserActivityStatsRepository
	extends MongoRepository<UserActivityStatsDocument, String> {

	// 해당 사용자의 가장 오래된 활동 문서
	Optional<UserActivityStatsDocument>
	findFirstByUserIdOrderByActivityDateAsc(String userId);

	// 현재 30일 주기에 포함되는 문서 조회
	@Query(
		value = """
			{
				'userId': ?0,
				'activityDate': {
					'$gte': ?1,
					'$lt': ?2
				}
			}
			""",
		sort = "{ 'activityDate': 1 }"
	)
	List<UserActivityStatsDocument>
	findActivityStatsByPeriod(
		String userId,
		Instant startDate,
		Instant endDate
	);
}